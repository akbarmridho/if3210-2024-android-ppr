package com.informatika.bondoman.model.repository.login

import com.informatika.bondoman.model.Resource

interface LoginRepository {
    suspend fun login(username: String, password: String): Resource<String>
}