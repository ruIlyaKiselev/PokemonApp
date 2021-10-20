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
import com.example.pokemonapp.network.PokeApiService
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import java.util.*

/*
*
*   This is repository module;
*   we use repository in viewModels (use case layer of clean architecture) for encapsulate database
*   and api service from viewModels.
*
*   Every getPokemon* function haves online and offline mode; Once downloaded pokemons stored in
*   cache set
*
* */
class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService,
    private val database: PokemonAppDatabase
): PokemonRepository {

    /*
    *   When we download pokemon from api once, we save it here; Then we don't need download same
    *   pokemon second time, we can tage it from here.
    * */
    private var cachedPokemons: MutableSet<Pokemon> = sortedSetOf(PokemonSetComparator())

    /*
    *   rxJava PublishSubject that we use in viewModels and fragments
    *   In this class PublishSubject emits valuer when we create page source
    *   Page source emits his own values and this class react to them
    * */
    private var pokemonsSubject = PublishSubject.create<Pokemon>()

    /*
    *   Comparator for save ordering cachedPokemons by id
    * */
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

    /*
    *   Function for loading list of pokemon (domain model) from api or from database
    *   haves signature like https://pokeapi.co/api/v2/pokemon?limit={limit}&offset={offset}
    * */
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

    /*
    *   Function for loading pokemon (domain model) from api or from database by id
    * */
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

    /*
    *  Method for getting pokemon pager; Can use few times because user can click floating button
    *  in list screen few times
    * */
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

    /*
    *   This function creates new page source by page from arguments; we use it when click floating
    *   button on list fragment
    * */
    fun initPageSource() {
        pokeApiPageSource?.pokemonsSubject
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(Schedulers.io())
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

    /*
    *    Dot't use it, I have tried use it when we click floating button in main screen very fast
    *    for stop threads from previous loading, but it's unstable
    * */
    override fun stopLoading() {
        listOfJob.forEach {
            it.cancel()
        }
    }

    override fun getTotalPokemonsCount(): Int = pokeApiPageSource?.totalPagesOnServer ?: 1

    /*
    *       Method for check connection (api can be not available)
    * */
    @Throws(InterruptedException::class, IOException::class)
    private fun checkConnection(): Boolean {
        val command = "ping -c 1 pokeapi.co"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}