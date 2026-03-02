package com.aventique.sephora.features.detail

import com.aventique.sephora.domain.model.Product

sealed class DetailUiState {
    data object Loading : DetailUiState()
    data class Success(val product: Product) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}