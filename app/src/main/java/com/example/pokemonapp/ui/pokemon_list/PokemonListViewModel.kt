package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiPageSource
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val pokeApiPageSource: PokeApiPageSource
): ViewModel() {

    private var mutablePokemonList = MutableLiveData<List<PokemonPreview>>()
    val pokemonList: LiveData<List<PokemonPreview>> = mutablePokemonList

    private var mutablePokemonDetails = MutableLiveData<Pokemon>()
    val pokemonDetails: LiveData<Pokemon> = mutablePokemonDetails

    val pokemons: StateFlow<PagingData<PokemonPreview>> = Pager<Int, PokemonPreview>(
        PagingConfig(pageSize = PokeApiContract.ITEMS_PER_PAGE)
    ) {
        pokeApiPageSource
    }.flow
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())


    private var currentPage = 0

    fun loadPokemonList() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("MyLog","CoroutineExceptionHandler got $exception")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            mutablePokemonList.postValue(
                pokemonRepository.loadPokemonList(
                    PokeApiContract.ITEMS_PER_PAGE,
                    0
                )
            )
        }
    }

    fun loadPokemonDetails(pokemonId: Int) {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("MyLog","CoroutineExceptionHandler got $exception")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            mutablePokemonDetails.postValue(
                pokemonRepository.loadPokemonDetailsById(pokemonId)
            )
        }
    }

    fun loadPokemonListPaginated() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("MyLog","CoroutineExceptionHandler got $exception")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            val loadedPage = pokemonRepository.loadPokemonList(
                limit = PokeApiContract.ITEMS_PER_PAGE,
                offset = currentPage * PokeApiContract.ITEMS_PER_PAGE
            )

            mutablePokemonList.value = mutablePokemonList.value?.plus(loadedPage)
        }

    }
}