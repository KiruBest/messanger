package com.example.messanger.app.di

import com.example.messanger.data.repository.AccountService
import com.example.messanger.domain.repository.IAccountService
import org.koin.dsl.module

val domainModule = module {
    factory<IAccountService> { AccountService(get()) }
}