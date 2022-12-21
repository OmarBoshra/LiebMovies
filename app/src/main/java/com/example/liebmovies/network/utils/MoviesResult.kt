package com.example.liebmovies.network.utils

sealed class MoviesResult<out T> {
    data class Success<out T>(val data: T) : MoviesResult<T>()
    data class Error(val errorMessage: String?) : MoviesResult<Nothing>()
}