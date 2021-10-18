package com.example.pokemonapp.domain

data class Pokemon (
    var id: Int?,
    var pokemonName: String?,
    var imageUrl: String?
) {
    var height: Int? = null
    var weight: Int? = null
    var type: List<String?> = listOf()
    var stats: Stats? = null
}