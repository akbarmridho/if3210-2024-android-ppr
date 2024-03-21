package com.informatika.bondoman.di

import com.informatika.bondoman.model.repository.login.LoginRepository
import com.informatika.bondoman.model.repository.login.LoginRepositoryImpl
import com.informatika.bondoman.model.repository.token.TokenRepository
import com.informatika.bondoman.model.repository.token.TokenRepositoryImpl
import com.informatika.bondoman.model.repository.transaction.TransactionRepository
import com.informatika.bondoman.model.repository.transaction.TransactionRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    // Dependency: LoginRepository
    single<LoginRepository> {
        LoginRepositoryImpl(get())
    }

    single<TokenRepository> {
        TokenRepositoryImpl(get())
    }

    // Dependency: TransactionRepository
    single<TransactionRepository> {
        TransactionRepositoryImpl(get())
    }

}