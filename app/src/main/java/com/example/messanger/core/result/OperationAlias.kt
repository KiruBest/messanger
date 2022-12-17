package com.example.messanger.core.result

import kotlinx.coroutines.flow.Flow

typealias ListResult<T> = OperationResult<List<T>>
typealias FlowResult<T> = Flow<OperationResult<T>>
typealias FlowListResult<T> = Flow<OperationResult<List<T>>>