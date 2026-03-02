package com.aventique.sephora.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.getErrorMessage
import com.aventique.sephora.domain.usecase.GetAllProductsUseCase
import com.aventique.sephora.domain.common.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAllProductsUseCase: GetAllProductsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getAllProductsUseCase(
                query = _uiState.value.searchQuery,
                sortOrder = _uiState.value.sortOrder
            ).collect { result ->
                when (result) {
                    is DataResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            products = result.data,
                            isLoading = false,
                            error = null,
                        )
                    }

                    is DataResult.Failure -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.error.getErrorMessage(),
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadProducts()
    }

    fun toggleSortOrder() {
        val newOrder = if (_uiState.value.sortOrder == SortOrder.BEST_TO_WORST)
            SortOrder.WORST_TO_BEST
        else
            SortOrder.BEST_TO_WORST
        _uiState.value = _uiState.value.copy(sortOrder = newOrder)
        loadProducts()
    }

    fun toggleExpanded(productId: Long) {
        val current = _uiState.value.expandedProductId
        _uiState.value = _uiState.value.copy(
            expandedProductId = if (current == productId) null else productId,
        )
    }

    fun onRefresh() {
        loadProducts()
    }
}
