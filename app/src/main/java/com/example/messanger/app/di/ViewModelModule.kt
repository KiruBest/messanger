package com.example.messanger.app.di

import com.example.messanger.presentation.activity.MainActivityViewModel
import com.example.messanger.presentation.fragment.authentication.LoginViewModel
import com.example.messanger.presentation.fragment.chat.ChatViewModel
import com.example.messanger.presentation.fragment.home.HomeViewModel
import com.example.messanger.presentation.fragment.home.settings.AccountSettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainActivityViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { parameters -> ChatViewModel(companionID = parameters.get(), get()) }
    viewModel { AccountSettingsViewModel(get()) }
}