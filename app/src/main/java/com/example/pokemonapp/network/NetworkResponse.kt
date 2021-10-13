package com.example.pokemonapp.network

sealed class NetworkResponse<T>(
    val content: T?,
    val message: String?
) {
    class Success<T>(content: T): NetworkResponse<T>(content, "")
    class Error<T>(content: T, message: String): NetworkResponse<T>(content, message)
    class Loading<T>: NetworkResponse<T>(null, "")
}
