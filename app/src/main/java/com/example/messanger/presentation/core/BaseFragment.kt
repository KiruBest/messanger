package com.example.messanger.presentation.core

import androidx.fragment.app.Fragment
import com.example.messanger.domain.core.UserState
import com.example.messanger.presentation.fragment.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseFragment: Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onStart() {
        super.onStart()
        viewModel.updateUserState(UserState.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        viewModel.updateUserState(UserState.OFFLINE)
    }
}