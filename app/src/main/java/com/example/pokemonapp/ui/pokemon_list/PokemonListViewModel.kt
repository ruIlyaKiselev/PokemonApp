package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
): ViewModel() {

    private var mutablePokemonList = MutableLiveData<MutableList<PokemonPreview>>()
    val pokemonList: LiveData<MutableList<PokemonPreview>> = mutablePokemonList

    private var pokemonPager = pokemonRepository.getPokemonPager(1)
    var pokemons: StateFlow<PagingData<PokemonPreview>> = getPokemonsStateFlow()

    var storedPokemons = pokemonRepository.getStoredPokemons()

    private fun getPokemonsStateFlow(): StateFlow<PagingData<PokemonPreview>>  {
        val stateFlow = pokemonPager
            .flow
            .cachedIn(viewModelScope)
            .onEach {
                Log.d("MyLog", mutablePokemonList.value?.map { it.id }.toString())
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = PagingData.empty()
            )
        return stateFlow
    }

    fun resetPokemonPagerRandomly() {
        pokemonPager = pokemonRepository.getPokemonPager((1..20).random())
        pokemons = getPokemonsStateFlow()
    }

}