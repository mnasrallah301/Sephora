package com.aventique.sephora.data.common

import com.aventique.sephora.domain.common.DataResult
import com.aventique.sephora.domain.common.ErrorType
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> safeCall(
    crossinline block: suspend () -> T
): DataResult<T> {
    return try {
        DataResult.Success(block())
    } catch (e: Exception) {
        DataResult.Failure(e.toErrorType())
    }
}

fun Throwable.toErrorType(): ErrorType = when (this) {
    is IOException -> ErrorType.Network
    is HttpException -> when (code()) {
        401 -> ErrorType.Unauthorized
        404 -> ErrorType.NotFound
        else -> ErrorType.Unknown(this)
    }

    else -> ErrorType.Unknown(this)
}