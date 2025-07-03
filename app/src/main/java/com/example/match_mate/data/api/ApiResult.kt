package com.example.match_mate.data.api

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String? = null) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}
