package com.aventique.sephora.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.common.getErrorMessage
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.usecase.GetAllProductsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.BEST_TO_WORST)
    private val _expandedProductId = MutableStateFlow<Long?>(null)
    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val requestFlow: Flow<DataResult<List<Product>>?> = merge(
        _searchQuery.flatMapLatest { query ->
            flow {
                emit(null) // loading
                getAllProductsUseCase(query).collect { emit(it) }
            }
        },
        _refreshTrigger.drop(1).flatMapLatest {
            flow {
                _isRefreshing.value = true
                val query = _searchQuery.value
                getAllProductsUseCase(query, forceRefresh = true)
                    .collect { emit(it) }
                _isRefreshing.value = false
            }
        }
    )

    val uiState: StateFlow<HomeUiState> = combine(
        requestFlow,
        _sortOrder,
        _searchQuery,
        _expandedProductId,
        _isRefreshing
    ) { result, sortOrder, query, expandedId, isRefreshing ->
        when {
            result == null && !isRefreshing -> HomeUiState.Loading

            result is DataResult.Success -> {
                val sorted = applySort(result.data, sortOrder)
                if (sorted.isEmpty()) {
                    HomeUiState.Empty(searchQuery = query, isRefreshing = isRefreshing)
                } else {
                    HomeUiState.Success(
                        products = sorted,
                        expandedProductId = expandedId,
                        sortOrder = sortOrder,
                        searchQuery = query,
                        isRefreshing = isRefreshing
                    )
                }
            }

            result is DataResult.Failure -> {
                HomeUiState.Error(
                    message = result.error.getErrorMessage(),
                    searchQuery = query,
                    isRefreshing = isRefreshing
                )
            }

            else -> HomeUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading,
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun toggleSortOrder() {
        _sortOrder.value = if (_sortOrder.value == SortOrder.BEST_TO_WORST)
            SortOrder.WORST_TO_BEST else SortOrder.BEST_TO_WORST
    }

    fun toggleExpanded(productId: Long) {
        _expandedProductId.value = if (_expandedProductId.value == productId) null else productId
    }

    fun onRefresh() {
        _refreshTrigger.value++
    }

    private fun applySort(items: List<Product>, sortOrder: SortOrder): List<Product> =
        items.map { product ->
            product.copy(
                reviews = when (sortOrder) {
                    SortOrder.BEST_TO_WORST -> product.reviews.sortedByDescending { it.rating }
                    SortOrder.WORST_TO_BEST -> product.reviews.sortedBy { it.rating }
                }
            )
        }
}
