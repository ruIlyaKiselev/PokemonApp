package com.example.pokemonapp.network.model.pokemons_list

import com.google.gson.annotations.SerializedName

data class PokemonsListDto(
    @SerializedName("count")
    val count: Int?,
    @SerializedName("next")
    val next: String?,
    @SerializedName("previous")
    val previous: Any?,
    @SerializedName("results")
    val results: List<Result>?
)