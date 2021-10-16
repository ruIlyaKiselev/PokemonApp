package com.example.pokemonapp.domain

data class PokemonPreview (
    val id: Int?,
    val pokemonName: String?,
    val imageUrl: String?
) {
    var loadedFullInfo: Boolean = id!! % 2 == 0
}
