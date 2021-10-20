package com.example.pokemonapp.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.database.entity.AppInfoEntity
import com.example.pokemonapp.domain.Converters.toDomain
import com.example.pokemonapp.domain.Converters.toPreview
import com.example.pokemonapp.domain.PokemonPreview
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PokeApiPageSource(
    private val pokeApiService: PokeApiService,
    private val initialPage: Int,
    private val database: PokemonAppDatabase
): PagingSource<Int, PokemonPreview>() {

    var cachedPreviews: MutableSet<PokemonPreview> = sortedSetOf(PokemonPreviewSetComparator())
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

    class PokemonPreviewSetComparator: Comparator<PokemonPreview> {
        override fun compare(p0: PokemonPreview?, p1: PokemonPreview?): Int {
            if(p0 == null || p1 == null) {
                return 0;
            }

            return p0.id!!.compareTo(p1.id!!)
        }
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
        cachedPreviews.addAll(resultList)

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

    @Throws(InterruptedException::class, IOException::class)
    private fun checkConnection(): Boolean {
        val command = "ping -c 1 pokeapi.co"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }
}