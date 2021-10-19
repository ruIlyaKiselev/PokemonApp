package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.domain.getStatsSum
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
): ViewModel() {

    private var mutablePokemonList = MutableLiveData<MutableList<Pokemon>>()
    val pokemonList: LiveData<MutableList<Pokemon>> = mutablePokemonList

    private var pokemonPager = pokemonRepository.getPokemonPager(1)
    var pokemons: StateFlow<PagingData<PokemonPreview>> = getPokemonsStateFlow()

    val pokemonsObservable = pokemonRepository.getPokemonsSubject()

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
//        pokemonRepository.stopLoading()
        pokemonPager = pokemonRepository.getPokemonPager((1..20).random())
        pokemons = getPokemonsStateFlow()
    }

    fun sortPokemons(
        byAttack: Boolean,
        byDefence: Boolean,
        byHp: Boolean
    ): List<Pokemon> {

        val sortedList = pokemonRepository.getStoredPokemons().sortedWith(
            PokemonComparator(byAttack, byDefence, byHp)
        )

        return sortedList
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

            val p0Sum = p0.getStatsSum(byAttack, byDefence, byHp)
            val p1Sum = p1.getStatsSum(byAttack, byDefence, byHp)

            return p1Sum.compareTo(p0Sum)
        }
    }
}