package com.example.messanger.presentation.fragment

import androidx.lifecycle.viewModelScope
import com.example.messanger.core.result.OperationResult
import com.example.messanger.data.repository.IMessengerService
import com.example.messanger.presentation.fragment.base.BaseViewModel
import com.example.messanger.presentation.model.CallUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CallViewModel(
    private val messengerService: IMessengerService
) : BaseViewModel() {
    private val _listFlow: MutableStateFlow<OperationResult<CallUi>> =
        MutableStateFlow(OperationResult.Loading)
    val listFlow: StateFlow<OperationResult<CallUi>> = _listFlow.asStateFlow()

    fun getCall() {
        viewModelScope.launch {
//            _listFlow.value = messengerService.sendMessage("asdad", "asdad")
        }
    }
}