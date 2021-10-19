package com.example.pokemonapp.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.Converters.Companion.toPreloaded
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiPageSource
import com.example.pokemonapp.network.PokeApiService
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.TimeUnit

class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService
): PokemonRepository {
    private var cachedPokemons: MutableSet<Pokemon> = sortedSetOf(PokemonSetComparator())
    private var pokemonsSubject = PublishSubject.create<Pokemon>()

    class PokemonSetComparator: Comparator<Pokemon> {
        override fun compare(p0: Pokemon?, p1: Pokemon?): Int {
            if(p0 == null || p1 == null){
                return 0;
            }

            return if (p0.id != null && p1.id != null) {
                p0.id!!.compareTo(p1.id!!)
            } else 0

        }
    }

    private var pokeApiPageSource: PokeApiPageSource? = null

    val listOfJob: MutableList<Job> = mutableListOf()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    override suspend fun loadPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return try {
            pokeApiService.getPokemonList(limit, offset).toPreloaded()
        } catch (e: Exception) {
            Log.e("MyLog", e.toString())
            listOf()
        }
    }

    override suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon {
        val restoredPokemon = cachedPokemons.filter { pokemon -> pokemon.id == pokemonId }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            var loadedPokemon: Pokemon? = null
            try {
                loadedPokemon = pokeApiService.getPokemonDetailsById(pokemonId).toDomain()
                cachedPokemons.add(loadedPokemon)

            } catch (e: Exception) {
                Log.e("MyLog", e.toString())
            }
            loadedPokemon ?: Pokemon(null, null, null)
        }
    }

    override suspend fun loadPokemonDetailsByName(pokemonName: String): Pokemon {
        val restoredPokemon = cachedPokemons.filter { pokemon -> pokemon.pokemonName == pokemonName }
        return if (restoredPokemon.isNotEmpty()) {
            restoredPokemon[0]
        } else {
            var loadedPokemon: Pokemon? = null
            try {
                loadedPokemon = pokeApiService.getPokemonDetailsByName(pokemonName).toDomain()
                cachedPokemons.add(loadedPokemon)

            } catch (e: Exception) {
                Log.e("MyLog", e.toString())
            }
            loadedPokemon ?: Pokemon(0, "", "")
        }
    }

    override fun getPokemonPager(initialPage: Int): Pager<Int, PokemonPreview> {

        pokeApiPageSource = PokeApiPageSource(pokeApiService, initialPage)

        initPageSource()

        return Pager(
            PagingConfig(
                pageSize = PokeApiContract.ITEMS_PER_PAGE,
                initialLoadSize = PokeApiContract.ITEMS_PER_PAGE,
            )
        ) {
            pokeApiPageSource!!
        }
    }

    fun initPageSource() {
        pokeApiPageSource?.pokemonsSubject
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
            ?.delay(50, TimeUnit.MILLISECONDS)
            ?.subscribe(
            {
                Log.d("MyLog", "Update!!!")
                val job = ioScope.launch {
                    mutex.withLock {
                        if (it.id != null) {
                            val loadedPokemon = loadPokemonDetailsById(it.id!!)
                            cachedPokemons.add(loadedPokemon)
                            pokemonsSubject.onNext(loadedPokemon)
                        }
                    }
                }

                listOfJob.add(job)
            },
            {
                Log.e("MyLog", it.toString())
            }
        )
    }

    override fun getPokemonsSubject(): PublishSubject<Pokemon> = pokemonsSubject
    override fun getStoredPokemons(): Set<Pokemon> = cachedPokemons
    override fun clearStoredPokemons() = cachedPokemons.clear()

    override fun stopLoading() {
        listOfJob.forEach {
            it.cancel()
        }
    }
}