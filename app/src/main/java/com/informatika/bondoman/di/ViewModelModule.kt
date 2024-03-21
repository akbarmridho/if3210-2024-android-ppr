package com.informatika.bondoman.di

import com.informatika.bondoman.prefdatastore.JWTManager
import com.informatika.bondoman.prefdatastore.JWTManagerImpl
import com.informatika.bondoman.viewmodel.JWTViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import com.informatika.bondoman.viewmodel.login.LoginViewModel
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.ListTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    // Dependency: JWTViewModel
    viewModel {
        JWTViewModel(JWTManagerImpl(androidContext()), get())
    }

    // Dependency: LoginViewModel
    viewModel<LoginViewModel> {
        LoginViewModel(get())
    }

    // Dependency: CreateTransactionViewModel
    viewModel {
        CreateTransactionViewModel(get())
    }

    // Dependency: DetailTransactionViewModel
    viewModel {
        parameters -> DetailTransactionViewModel(get(), _id = parameters.get())
    }

    // Dependency: UpdateTransactionViewModel
    viewModel {
        parameters -> UpdateTransactionViewModel(get(), _id = parameters.get())
    }

    // Dependency: ListTransactionViewModel
    viewModel {
        ListTransactionViewModel(get())
    }

}