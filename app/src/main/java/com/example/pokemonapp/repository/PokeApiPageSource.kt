package com.example.pokemonapp.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.database.entity.AppInfoEntity
import com.example.pokemonapp.domain.Converters.toDomain
import com.example.pokemonapp.domain.Converters.toPreview
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiService
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
/*
*
*   This is page source class for recyclerView; part of jetpack paging3
*   Class supports online and offline mode, it pings pokeapi.co and depending from the result
*   use online or offline mode.
*
*   When class downloads info from network, it emits this info to rxJava PublishSubject;
*   Repository is listening PublishSubject from here and viewModels are listening
*   PublishSubject from Repository; When something emits to PublishSubject, fragments associated
*   with viewModels, react to this event (you can see in in PokemonListFragment).
*
*   When class downloads info from network, it stored new info to database as well.
* */

class PokeApiPageSource(
    private val pokeApiService: PokeApiService,
    private val initialPage: Int,
    private val database: PokemonAppDatabase
): PagingSource<Int, PokemonPreview>() {

    var pokemonsSubject = PublishSubject.create<PokemonPreview>()

    var totalPagesOnServer: Int = 0
        set(value) {
            if (value != 0) {
                field = value
            }
        }

    var totalPokemonsCount = 0
        set(value) {
            if (value != 0) {
                field = value
            }
        }

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var isConnected: Boolean

    init {
        isConnected = checkConnection()
    }

    override fun getRefreshKey(state: PagingState<Int, PokemonPreview>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null

        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokemonPreview> {
        if (initialPage <= 0) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        val page: Int = params.key ?: initialPage
        val pageSize: Int = params.loadSize.coerceAtLeast(PokeApiContract.ITEMS_PER_PAGE)

        return if (isConnected) {
            loadFromApi(pageSize, page)
        } else {
            loadFromDatabase(pageSize, page)
        }
    }

    /*
    *       Method for loading page from  API (online mode)
    * */
    private suspend fun loadFromApi(pageSize: Int, page: Int): LoadResult.Page<Int, PokemonPreview> {
        val response = try {
            pokeApiService.getPokemonList(limit = pageSize, offset = (page - 1) * pageSize)
        } catch (e: Exception) {
            null
        }

        totalPokemonsCount = response?.count ?: 0

        if (totalPagesOnServer == 0) {
            ioScope.async {
                saveAppInfoToDatabase()
            }
        }

        totalPagesOnServer = totalPokemonsCount / (PokeApiContract.ITEMS_PER_PAGE) + 1

        val resultList = response?.toDomain() ?: listOf()

        resultList.forEach {
            pokemonsSubject.onNext(it)
        }

        val nextPage: Int? = if (response?.results != null) {
            if (response.results.size < pageSize) null else page + 1
        } else {
            null
        }

        val prevPage = if (page <= 1) null else page - 1

        return LoadResult.Page(resultList, prevPage, nextPage)
    }

    /*
    *   Method for loading page from database (offline mode)
    * */
    private suspend fun loadFromDatabase(pageSize: Int, page: Int) : LoadResult.Page<Int, PokemonPreview> {
        totalPokemonsCount = database.dao.getAppInfo().last().countOfElements ?: 0
        totalPagesOnServer = totalPokemonsCount / (PokeApiContract.ITEMS_PER_PAGE) + 1

        val resultList = database.dao.getPokemonsPage(page, pageSize).map { it.toDomain().toPreview() }

        resultList.forEach {
            pokemonsSubject.onNext(it)
        }

        val nextPage: Int? = if (resultList.size < pageSize) null else page + 1
        val prevPage = if (page <= 1) null else page - 1

        return LoadResult.Page(resultList, prevPage, nextPage)
    }

    /*
    * Method for savind appInfo info (total count of pokemons from server and timestamp of save)
    * */
    private suspend fun saveAppInfoToDatabase() {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        database.dao.deleteAppInfo()
        database.dao.insertAppInfo(
            AppInfoEntity(
                currentDate,
                totalPokemonsCount
            )
        )
    }

    /*
    *       Method for check connection (api can be not available)
    * */
    @Throws(InterruptedException::class, IOException::class)
    private fun checkConnection(): Boolean {
        val command = "ping -c 1 pokeapi.co"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}