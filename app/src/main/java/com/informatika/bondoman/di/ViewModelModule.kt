package com.informatika.bondoman.di

import com.informatika.bondoman.viewmodel.JWTViewModel
import com.informatika.bondoman.viewmodel.connectivity.ConnectivityViewModel
import com.informatika.bondoman.viewmodel.login.LoginViewModel
import com.informatika.bondoman.viewmodel.scanner.ScanPreviewViewModel
import com.informatika.bondoman.viewmodel.transaction.CreateTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.DetailTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.ExporterTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.ListTransactionViewModel
import com.informatika.bondoman.viewmodel.transaction.UpdateTransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Dependency: ConnectivityViewModel
    viewModel {
        ConnectivityViewModel(get())
    }

    // Dependency: JWTViewModel
    viewModel {
        JWTViewModel(get(), get())
    }

    // Dependency: LoginViewModel
    viewModel {
        LoginViewModel(get())
    }

    // Dependency: CreateTransactionViewModel
    viewModel {
        CreateTransactionViewModel(get())
    }

    // Dependency: DetailTransactionViewModel
    viewModel { parameters ->
        DetailTransactionViewModel(get(), transaction = parameters.get())
    }

    // Dependency: UpdateTransactionViewModel
    viewModel { parameters ->
        UpdateTransactionViewModel(get(), transaction = parameters.get())
    }

    // Dependency: ListTransactionViewModel
    viewModel {
        ListTransactionViewModel(get())
    }

    // Dependency: ExporterTransactionViewModel
    viewModel {
        ExporterTransactionViewModel(get())
    }

    // Dependency: ScanPreviewViewModel
    viewModel {
        ScanPreviewViewModel(get(), get())
    }
}