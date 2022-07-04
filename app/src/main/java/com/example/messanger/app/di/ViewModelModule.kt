package com.example.messanger.app.di

import com.example.messanger.presentation.fragment.authentication.LoginViewModel
import com.example.messanger.presentation.fragment.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
}