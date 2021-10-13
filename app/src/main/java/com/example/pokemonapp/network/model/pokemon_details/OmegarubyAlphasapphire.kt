package com.example.pokemonapp.network.model.pokemon_details


import com.google.gson.annotations.SerializedName

data class OmegarubyAlphasapphire(
    @SerializedName("front_default")
    val frontDefault: String?,
    @SerializedName("front_female")
    val frontFemale: String?,
    @SerializedName("front_shiny")
    val frontShiny: String?,
    @SerializedName("front_shiny_female")
    val frontShinyFemale: String?
)