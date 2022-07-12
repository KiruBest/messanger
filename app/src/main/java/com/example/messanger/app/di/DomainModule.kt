package com.example.messanger.app.di

import com.example.messanger.data.repository.AccountService
import com.example.messanger.data.repository.MessengerService
import com.example.messanger.domain.repository.IAccountService
import com.example.messanger.domain.repository.IMessengerService
import org.koin.dsl.module

val domainModule = module {
    single<IAccountService> { AccountService(get(), get()) }
    single<IMessengerService> { MessengerService(get(), get()) }
}