package com.playground.starwars.datasource

sealed class Result<out T : Any> {
    data class Success<T : Any>(val data: T) : Result<T>()
    data class Error(val error: String) : Result<Nothing>()
}