package com.example.pokemonapp.domain

data class Pokemon (
    val id: Int?,
    val pokemonName: String?,
    val imageUrl: String?,
    val height: Int?,
    val weight: Int?,
    val type: List<String?>,
    val stats: Stats?
)