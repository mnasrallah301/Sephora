package com.aventique.sephora.features.home

import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product

data class HomeUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.BEST_TO_WORST,
    val expandedProductId: Long? = null,
)