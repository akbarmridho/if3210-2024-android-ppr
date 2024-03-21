package com.informatika.bondoman.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Resource<out T> {

    class Success<out T>(val data: T) : Resource<T>()
    class Error<out T>(val  throwable: Throwable) : Resource<T>()
    class Loading<out T> : Resource<T>()
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$throwable]"
            is Loading -> "Loading"
        }
    }
}