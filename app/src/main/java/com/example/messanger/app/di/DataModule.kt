package com.example.messanger.app.di

import com.example.messanger.data.repository.AccountService
import com.example.messanger.data.repository.IAccountService
import com.example.messanger.data.repository.IMessengerService
import com.example.messanger.data.repository.MessengerService
import com.google.gson.Gson
import org.koin.dsl.module

val dataModule = module {
    single<IAccountService> { AccountService(get(), get(), get(), get()) }
    single<IMessengerService> { MessengerService(get(), get(), get(), get(), get()) }
    single { Gson() }
}