package com.informatika.bondoman.model.repository.token

import com.informatika.bondoman.model.Resource

interface TokenRepository {
    suspend fun token(token: String): Resource<Boolean>
}