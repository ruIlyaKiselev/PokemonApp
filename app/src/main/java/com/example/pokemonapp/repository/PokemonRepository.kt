package com.example.pokemonapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview

interface PokemonRepository {
    suspend fun loadPokemonList(limit: Int, offset: Int): List<Pokemon>
    suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon
    suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon

    fun getPokemonPager(initialPage: Int): Pager<Int, Pokemon>
    fun getStoredPokemons(): MutableLiveData<MutableSet<Pokemon>>
    fun clearStoredPokemons()
}