package com.example.pokemonapp.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.PokemonPreview
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

class PokeApiPageSource(
    private val pokeApiService: PokeApiService,
    private val initialPage: Int
): PagingSource<Int, PokemonPreview>() {

    var cachedPreviews: MutableSet<PokemonPreview> = sortedSetOf(PokemonPreviewSetComparator())
    var pokemonsSubject = PublishSubject.create<PokemonPreview>()

    class PokemonPreviewSetComparator: Comparator<PokemonPreview> {
        override fun compare(p0: PokemonPreview?, p1: PokemonPreview?): Int {
            if(p0 == null || p1 == null){
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

        val response = try {
            pokeApiService.getPokemonList(limit = pageSize, offset = (page - 1) * pageSize)
        } catch (e: Exception) {
            null
        }

        val resultList = response?.toDomain() ?: listOf()
        cachedPreviews.addAll(resultList)

        resultList.forEach {
            pokemonsSubject.onNext(it)
        }

        var nextPage: Int? = 0

        nextPage = if (response?.results != null) {
            if (response.results.size < pageSize) null else page + 1
        } else {
            null
        }

        val prevPage = if (page <= 1) null else page - 1

        return LoadResult.Page(resultList, prevPage, nextPage)
    }
}