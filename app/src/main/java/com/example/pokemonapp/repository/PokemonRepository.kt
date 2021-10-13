package com.example.pokemonapp.repository

import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview

interface PokemonRepository {
    suspend fun loadPokemonList(limit: Int, offset: Int): List<PokemonPreview>
    suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon
    suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon
}