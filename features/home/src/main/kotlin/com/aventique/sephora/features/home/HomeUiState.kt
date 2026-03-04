package com.aventique.sephora.features.home

import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product

sealed interface HomeUiState {
    val searchQuery: String get() = ""
    val isRefreshing: Boolean get() = false
    data object Loading : HomeUiState
    data class Empty(
        override val searchQuery: String,
        override val isRefreshing: Boolean = false
    ) : HomeUiState
    data class Success(
        val products: List<Product>,
        val expandedProductId: Long?,
        val sortOrder: SortOrder,
        override val searchQuery: String,
        override val isRefreshing: Boolean = false,
    ) : HomeUiState
    data class Error(
        val message: String,
        override val searchQuery: String,
        override val isRefreshing: Boolean = false
    ) : HomeUiState
}
