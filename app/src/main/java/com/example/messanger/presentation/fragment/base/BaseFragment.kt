package com.example.messanger.presentation.fragment.base

import androidx.fragment.app.Fragment
import com.example.messanger.core.enumeration.UserState
import com.example.messanger.presentation.fragment.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment: Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        /* todo Дичь, надо у MainActivity создать ViewModel и скопировать туда эти функции
        Соответвенно их туда перенести
         */
        viewModel.updateUserState(UserState.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateUserState(UserState.OFFLINE)
    }
}