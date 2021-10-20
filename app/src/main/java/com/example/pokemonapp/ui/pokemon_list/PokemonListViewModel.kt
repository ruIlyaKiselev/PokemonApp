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
    private val pokemonRepository: PokemonRepository
): ViewModel() {

    private var pokemonPager = pokemonRepository.getPokemonPager(1)
    var pokemons: StateFlow<PagingData<PokemonPreview>> = getPokemonsStateFlow()

    val pokemonsObservable: PublishSubject<Pokemon> = pokemonRepository.getPokemonsSubject()

    /*
    *   Method provides stateFlow from pokemon pager. PokemonListFragment subscribes to this
    *   and react to new values (part of paging)
    * */
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

    /*
    *    This function for reset page source for recyclerView when we click floating button
    *    By default recyclerView have page 1, but this method moves recyclerView to random page
    *    from 1 to maxCountOfPokemonsFromApi / pageSize + 1
    * */
    fun resetPokemonPagerRandomly() {
        var totalPokemonsCount = pokemonRepository.getTotalPokemonsCount()
        if (totalPokemonsCount == 0) totalPokemonsCount = 1

        val randomNumberOfPage = (1..totalPokemonsCount).random()

//        pokemonRepository.stopLoading()
        Log.d("MyLog", totalPokemonsCount.toString())
        pokemonPager = pokemonRepository.getPokemonPager(randomNumberOfPage)
        pokemons = getPokemonsStateFlow()
    }

    /*
    *       This function for getting list of pokemons with order according to sum of selected specs
    * */
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

    /*
    *   Comparator for combine all combination of checkboxes in listFragment
    * */
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