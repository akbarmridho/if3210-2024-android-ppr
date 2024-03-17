//package com.informatika.bondoman.utils.jwt
//
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.Preferences
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//
//class JWTViewModelFactory(private val dataStore: DataStore<Preferences>): ViewModelProvider.Factory {
//
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//
//        if (modelClass.isAssignableFrom(JWTViewModel::class.java)) {
//            return JWTViewModel(
//                jwtManager = JWTManager(context = dataStore)
//            ) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//
//
//}