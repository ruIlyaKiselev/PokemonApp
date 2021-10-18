package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    var storedPokemons: MutableLiveData<MutableSet<Pokemon>> = pokemonRepository.getStoredPokemons()

    init {
        storedPokemons.observeForever {
            viewModelScope.launch {
                delay(2000)
                storedPokemons.postValue(storedPokemons.value)
            }
        }
    }

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

    fun sortPokemons(
        byAttack: Boolean,
        byDefence: Boolean,
        byHp: Boolean
    ): List<Int> {

        val sortedList = storedPokemons.value?.sortedWith(
            PokemonComparator(byAttack, byDefence, byHp)
        )

        return sortedList?.map { it.id!! } ?: listOf()
    }

    private class PokemonComparator(
        private val byAttack: Boolean,
        private val byDefence: Boolean,
        private val byHp: Boolean
    ): Comparator<Pokemon> {
        override fun compare(p0: Pokemon?, p1: Pokemon?): Int {

            if(p0 == null || p1 == null) {
                return 0;
            }

            var p0Sum = 0
            var p1Sum = 0

            if (byAttack) {
                p0Sum += p0.stats!!.attack!!
                p1Sum += p1.stats!!.attack!!
            }

            if (byDefence) {
                p0Sum += p0.stats!!.defence!!
                p1Sum += p1.stats!!.defence!!
            }

            if (byHp) {
                p0Sum += p0.stats!!.hp!!
                p1Sum += p1.stats!!.hp!!
            }

            return p0Sum.compareTo(p1Sum)
        }
    }
}