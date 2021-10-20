package com.example.pokemonapp.repository

import androidx.paging.Pager
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface PokemonRepository {
    suspend fun loadPokemonList(limit: Int, offset: Int): List<Pokemon>
    suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon

    fun getPokemonPager(initialPage: Int): Pager<Int, PokemonPreview>
    fun getPokemonsSubject(): PublishSubject<Pokemon>

    fun getStoredPokemons(): Set<Pokemon>
    fun clearStoredPokemons()

    fun stopLoading()

    fun getTotalPokemonsCount(): Int
}