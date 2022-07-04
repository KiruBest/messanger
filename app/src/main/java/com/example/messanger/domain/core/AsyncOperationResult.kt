package com.example.messanger.domain.core

import java.lang.Exception

sealed class AsyncOperationResult<out T> {
    class EmptyState<out T>: AsyncOperationResult<T>()
    class Loading<out T>: AsyncOperationResult<T>()
    data class Success<out T>(val data: T): AsyncOperationResult<T>()
    data class Failure<out T>(val exception: Exception): AsyncOperationResult<T>()
}