package com.example.pokemonapp.repository

import android.util.Log
import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiService

class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService
): PokemonRepository {
    private var storedPokemons: MutableList<Pokemon> = mutableListOf()

    override suspend fun loadPokemonList(limit: Int, offset: Int): List<PokemonPreview> {
        return pokeApiService.getPokemonList(limit, offset).toDomain()
    }

    override suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon {
        val restoredPokemon = storedPokemons.filter { pokemon -> pokemon.id == pokemonId }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            val loadedPokemon = pokeApiService.getPokemonDetailsById(pokemonId).toDomain()
            storedPokemons.add(loadedPokemon)
            loadedPokemon
        }
    }

    override suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon {
        val restoredPokemon = storedPokemons.filter { pokemon -> pokemon.pokemonName == pokemonName }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            val loadedPokemon = pokeApiService.getPokemonDetailsByName(pokemonName).toDomain()
            storedPokemons.add(loadedPokemon)
            loadedPokemon
        }
    }

    override fun getStoredPokemons(): MutableList<Pokemon> = storedPokemons.toMutableList()
    override fun clearStoredPokemons() = storedPokemons.clear()
}