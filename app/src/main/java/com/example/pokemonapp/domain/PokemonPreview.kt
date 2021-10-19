package com.example.pokemonapp.domain

data class PokemonPreview (
    var id: Int?,
    var pokemonName: String?,
    var imageUrl: String?
) {
    var loadedFullInfo: Boolean = false
    var isBest: Boolean = false
}
