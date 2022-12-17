@file:Suppress("UNCHECKED_CAST")

package com.example.messanger.core.extensions

import com.example.messanger.core.result.OperationResult

inline fun <reified I : Any, reified O : Any> OperationResult<I>.mapIfSuccess(map: (I) -> OperationResult<O>): OperationResult<O> {
    return if (this is OperationResult.Success) map(data)
    else this as OperationResult<O>
}

val <T : Any> OperationResult<T>.isSuccess: Boolean get() = this is OperationResult.Success
val <T : Any> OperationResult<T>.isLoading: Boolean get() = this is OperationResult.Loading
val <T : Any> OperationResult<T>.isEmpty: Boolean get() = this is OperationResult.Empty
val <T : Any> OperationResult<T>.isError: Boolean get() = this is OperationResult.Error