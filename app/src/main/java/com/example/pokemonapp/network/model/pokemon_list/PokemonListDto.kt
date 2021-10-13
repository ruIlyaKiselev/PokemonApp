package com.example.pokemonapp.network.model.pokemon_list

import com.google.gson.annotations.SerializedName

data class PokemonListDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("previous")
    val previous: Any?,
    @SerializedName("results")
    val results: List<Result>?
)