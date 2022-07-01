package com.example.messanger.app.di

import com.example.messanger.data.remote.AccountSource
import com.example.messanger.data.repository.IAccountSource
import org.koin.dsl.module

val dataModule = module {
    single<IAccountSource> { AccountSource(get()) }
}