package com.example.messanger.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messanger.core.enumeration.UserState
import kotlinx.coroutines.launch

class MainActivityViewModel(
//    private val accountService: IAccountService,
) : ViewModel() {
    fun updateUserState(state: UserState) {
        viewModelScope.launch {
//            accountService.updateUserState(state)
        }
    }
}