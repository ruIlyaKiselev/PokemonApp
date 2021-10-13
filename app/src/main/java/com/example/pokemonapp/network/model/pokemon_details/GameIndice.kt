package com.example.pokemonapp.network.model.pokemon_details


import com.google.gson.annotations.SerializedName

data class GameIndice(
    @SerializedName("game_index")
    val gameIndex: Int?,
    @SerializedName("version")
    val version: Version?
)