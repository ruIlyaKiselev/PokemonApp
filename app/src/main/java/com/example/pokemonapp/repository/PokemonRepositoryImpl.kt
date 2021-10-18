package com.example.pokemonapp.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.Converters.Companion.toPreloaded
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiPageSource
import com.example.pokemonapp.network.PokeApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService
): PokemonRepository {
    private var storedPokemons: MutableLiveData<MutableSet<Pokemon>> = MutableLiveData(
        sortedSetOf(PokemonSetComparator())
    )

    class PokemonSetComparator: Comparator<Pokemon> {
        override fun compare(p0: Pokemon?, p1: Pokemon?): Int {
            if(p0 == null || p1 == null){
                return 0;
            }

            return p0.id!!.compareTo(p1.id!!)
        }
    }

    private var pokeApiPageSource: PokeApiPageSource? = null

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    override suspend fun loadPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return pokeApiService.getPokemonList(limit, offset).toPreloaded()
    }

    override suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon {
        val restoredPokemon = storedPokemons.value!!.filter { pokemon -> pokemon.id == pokemonId }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            val loadedPokemon = pokeApiService.getPokemonDetailsById(pokemonId).toDomain()
            storedPokemons.value?.add(loadedPokemon)
            loadedPokemon
        }
    }

    override suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon {
        val restoredPokemon = storedPokemons.value!!.filter { pokemon -> pokemon.pokemonName == pokemonName }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            val loadedPokemon = pokeApiService.getPokemonDetailsByName(pokemonName).toDomain()
            storedPokemons.value?.add(loadedPokemon)
            loadedPokemon
        }
    }

    override fun getPokemonPager(initialPage: Int): Pager<Int, Pokemon> {

        pokeApiPageSource = PokeApiPageSource(pokeApiService, initialPage)

        pokeApiPageSource?.cachedPreviews?.observeForever { pokemonPreviewSet ->
            Log.d("MyLog", "Update!!!")
            pokemonPreviewSet.forEach { pokemonPreview ->
                ioScope.launch {
                    mutex.withLock {
                        storedPokemons.value!!.add(loadPokemonDetailsById(pokemonPreview.id ?: 0))
                    }
                }
            }
        }

        return Pager(
            PagingConfig(
                pageSize = PokeApiContract.ITEMS_PER_PAGE,
                initialLoadSize = PokeApiContract.ITEMS_PER_PAGE,
            )
        ) {
            pokeApiPageSource!!
        }
    }

    override fun getStoredPokemons(): MutableLiveData<MutableSet<Pokemon>> = storedPokemons
    override fun clearStoredPokemons() = storedPokemons.value!!.clear()
}