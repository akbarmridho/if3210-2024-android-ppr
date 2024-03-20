package com.informatika.bondoman.network

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class ApiResponse<out T : Any> {

    data class Success<out T : Any>(val data: T) : ApiResponse<T>()
    data class Error(val exception: Exception) : ApiResponse<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}