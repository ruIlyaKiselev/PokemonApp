package com.example.pokemonapp.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.domain.Converters.toDomain
import com.example.pokemonapp.domain.Converters.toEntity
import com.example.pokemonapp.domain.Converters.toPreloaded
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiPageSource
import com.example.pokemonapp.network.PokeApiService
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.*

class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService,
    private val database: PokemonAppDatabase
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

    private val listOfJob: MutableList<Job> = mutableListOf()
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private val isConnected: Boolean

    init {
        isConnected = checkConnection()
    }

    override suspend fun loadPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return if (isConnected) {
            try {
                pokeApiService.getPokemonList(limit, offset).toPreloaded()
            } catch (e: Exception) {
                Log.e("MyLog", e.toString())
                listOf()
            }
        } else {
            try {
                database.dao.getPokemonsPage(limit, offset).map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("MyLog", e.toString())
                listOf()
            }
        }
    }

    override suspend fun loadPokemonDetailsById(pokemonId: Int): Pokemon {
        val restoredPokemon = cachedPokemons.find { pokemon -> pokemon.id == pokemonId }

        return if (restoredPokemon != null) {
            restoredPokemon
        } else {
            return if (isConnected) {
                try {
                    pokeApiService.getPokemonDetailsById(pokemonId).toDomain()
                } catch (e: Exception) {
                    Log.e("MyLog", e.toString())
                    Pokemon(0, "", "")
                }
            } else {
                try {
                    database.dao.getPokemonById(pokemonId).toDomain()
                } catch (e: Exception) {
                    Log.e("MyLog", e.toString())
                    Pokemon(0, "", "")
                }
            }
        }
    }

    override fun getPokemonPager(initialPage: Int): Pager<Int, PokemonPreview> {

        pokeApiPageSource = PokeApiPageSource(pokeApiService, initialPage, database)

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
//            ?.delay(50, TimeUnit.MILLISECONDS)
            ?.subscribe(
            {
                val job = ioScope.launch {
                    mutex.withLock {
                        if (it.id != null) {
                            val loadedPokemon = loadPokemonDetailsById(it.id!!)
                            cachedPokemons.add(loadedPokemon)
                            pokemonsSubject.onNext(loadedPokemon)

                            async {
                                database.dao.insertPokemon(loadedPokemon.toEntity())
                            }
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

    override fun getTotalPokemonsCount(): Int = pokeApiPageSource?.totalPagesOnServer ?: 1

    @Throws(InterruptedException::class, IOException::class)
    private fun checkConnection(): Boolean {
        val command = "ping -c 1 pokeapi.co"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}