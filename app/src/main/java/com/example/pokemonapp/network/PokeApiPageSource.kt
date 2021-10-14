package com.example.pokemonapp.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokemonapp.domain.Converters.Companion.toDomain
import com.example.pokemonapp.domain.PokemonPreview

class PokeApiPageSource(
    private val pokeApiService: PokeApiService,
    private val initialPage: Int
): PagingSource<Int, PokemonPreview>() {

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

        val page: Int = params.key ?: 1
        val pageSize: Int = params.loadSize.coerceAtLeast(PokeApiContract.ITEMS_PER_PAGE)

        val response = pokeApiService.getPokemonList(limit = pageSize, offset = (page - 1) * pageSize)

        val resultList = response.toDomain()

        val nextPage = if (response.results!!.size < pageSize) null else page + 1
        val prevPage = if (page <= 1) null else page - 1

        return LoadResult.Page(resultList, prevPage, nextPage)
    }
}