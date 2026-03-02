package com.aventique.sephora.domain.common

sealed class DataResult<out T> {
    data class Success<out T>(val data: T) : DataResult<T>()
    data class Failure(val error: ErrorType) : DataResult<Nothing>()
}
