package com.informatika.bondoman.utils.jwt

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class JWTViewModelFactory(private val context: Context, ): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(JWTViewModel::class.java)) {
            return JWTViewModel(
                jwtManager = JWTManager(context, )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}