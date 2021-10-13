package com.example.pokemonapp.network.model.pokemon_details


import com.google.gson.annotations.SerializedName

data class OfficialArtwork(
    @SerializedName("front_default")
    val frontDefault: String?
)