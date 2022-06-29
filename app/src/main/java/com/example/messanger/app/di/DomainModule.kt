package com.example.messanger.app.di

import com.example.messanger.data.repository.Repository
import com.example.messanger.domain.repository.IRepository
import org.koin.dsl.module

val domainModule = module {
    factory<IRepository> { Repository() }
}