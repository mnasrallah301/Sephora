package com.aventique.sephora.features.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.getErrorMessage
import com.aventique.sephora.domain.usecase.GetProductByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val productId: Long,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        loadProduct(productId)
    }

    private fun loadProduct(id: Long) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            when (val result = getProductByIdUseCase(id)) {
                is DataResult.Success -> {
                    val product = result.data
                    _uiState.value = if (product != null) {
                        DetailUiState.Success(product)
                    } else {
                        DetailUiState.Error("Product not found")
                    }
                }

                is DataResult.Failure -> {
                    _uiState.value = DetailUiState.Error(result.error.getErrorMessage())
                }
            }
        }
    }

    fun onRetry() {
        loadProduct(productId)
    }
}