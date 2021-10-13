package com.example.pokemonapp.network.model.pokemon_details


import com.google.gson.annotations.SerializedName

data class GenerationVi(
    @SerializedName("omegaruby-alphasapphire")
    val omegarubyAlphasapphire: OmegarubyAlphasapphire?,
    @SerializedName("x-y")
    val xY: XY?
)