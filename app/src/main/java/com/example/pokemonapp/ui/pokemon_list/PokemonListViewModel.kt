package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pokemonapp.database.PokemonAppDatabase
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.domain.getStatsSum
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository,
    private val database: PokemonAppDatabase
): ViewModel() {

    private var pokemonPager = pokemonRepository.getPokemonPager(1)
    var pokemons: StateFlow<PagingData<PokemonPreview>> = getPokemonsStateFlow()

    val pokemonsObservable: PublishSubject<Pokemon> = pokemonRepository.getPokemonsSubject()

    private fun getPokemonsStateFlow(): StateFlow<PagingData<PokemonPreview>>  {
        val stateFlow = pokemonPager
            .flow
            .cachedIn(viewModelScope)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = PagingData.empty()
            )
        return stateFlow
    }

    fun resetPokemonPagerRandomly() {
        var totalPokemonsCount = pokemonRepository.getTotalPokemonsCount()
        if (totalPokemonsCount == 0) totalPokemonsCount = 1

        val randomNumberOfPage = (1..totalPokemonsCount).random()

//        pokemonRepository.stopLoading()
        Log.d("MyLog", totalPokemonsCount.toString())
        pokemonPager = pokemonRepository.getPokemonPager(randomNumberOfPage)
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