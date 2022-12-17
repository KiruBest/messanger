package com.example.messanger.core.result

sealed class OperationResult<out T> {
    object Empty : OperationResult<Nothing>()
    object Loading : OperationResult<Nothing>()
    data class Success<out T>(val data: T) : OperationResult<T>()
    data class Error<out T>(val exception: Exception) : OperationResult<T>()
}