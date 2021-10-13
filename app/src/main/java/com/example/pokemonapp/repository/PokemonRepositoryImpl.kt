package com.example.pokemonapp.repository

import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiService

class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService
): PokemonRepository {
    override suspend fun loadPokemonList(limit: Int, offset: Int): List<PokemonPreview> {
        return pokeApiService.getPokemonList(limit, offset).toDomain()
    }

    override suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon {
        return pokeApiService.getPokemonDetailsById(pokemonId).toDomain()
    }

    override suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon {
        return pokeApiService.getPokemonDetailsByName(pokemonName).toDomain()
    }
}