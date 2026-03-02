package com.aventique.sephora.di

import com.aventique.sephora.data.local.database.SephoraDatabase
import com.aventique.sephora.data.local.datasource.ProductLocalDataSource
import com.aventique.sephora.data.local.datasource.ProductLocalDataSourceImpl
import com.aventique.sephora.data.remote.datasource.ProductRemoteDataSource
import com.aventique.sephora.data.remote.datasource.ProductRemoteDataSourceImpl
import com.aventique.sephora.data.remote.network.ProductsApi
import com.aventique.sephora.data.repository.ProductRepositoryImpl
import com.aventique.sephora.domain.repository.ProductRepository
import com.aventique.sephora.domain.usecase.GetAllProductsUseCase
import com.aventique.sephora.domain.usecase.GetProductByIdUseCase
import com.aventique.sephora.features.detail.DetailViewModel
import com.aventique.sephora.features.home.HomeViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

fun appModule() = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideProductsApi(get()) }
    single { SephoraDatabase.getInstance(androidContext()) }
    single { get<SephoraDatabase>().productDao() }
    single<ProductRemoteDataSource> { ProductRemoteDataSourceImpl(get()) }
    single<ProductLocalDataSource> { ProductLocalDataSourceImpl(get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get()) }
    singleOf(::GetAllProductsUseCase)
    singleOf(::GetProductByIdUseCase)
    viewModel { HomeViewModel(get()) }
    viewModel { (productId: Long) -> DetailViewModel(get(), productId) }
}

private fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    return OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
}

private fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    val contentType = "application/json".toMediaType()
    val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    return Retrofit.Builder()
        .baseUrl("https://sephoraandroid.github.io/testProject/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}

private fun provideProductsApi(retrofit: Retrofit): ProductsApi {
    return retrofit.create(ProductsApi::class.java)
}

