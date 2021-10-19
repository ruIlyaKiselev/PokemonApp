package com.example.pokemonapp.ui.pokemon_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailsViewModel@Inject constructor(
    private val pokemonRepository: PokemonRepository
): ViewModel() {

    private var mutablePokemonDetails = MutableLiveData<Pokemon>()
    val pokemonDetails: LiveData<Pokemon> = mutablePokemonDetails

    fun loadPokemonDetails(pokemonId: Int) {
        val handler = getExceptionHandler()

        viewModelScope.launch(Dispatchers.IO + handler) {
            mutablePokemonDetails.postValue(
                pokemonRepository.loadPokemonDetailsById(pokemonId)
            )
        }
    }

    fun getMaxPokemonAttack(): Int {

        val storedPokemons: MutableList<Pokemon> = pokemonRepository.getStoredPokemons().toMutableList()
        storedPokemons.sortByDescending {
            it.stats?.attack
        }
        return storedPokemons[0].stats?.attack ?: 0
    }

    fun getMaxPokemonDefence(): Int {

        val storedPokemons: MutableList<Pokemon> = pokemonRepository.getStoredPokemons().toMutableList()
        storedPokemons.sortByDescending {
            it.stats?.defence
        }
        return storedPokemons[0].stats?.defence ?: 0
    }

    fun getMaxPokemonHp(): Int {

        val storedPokemons: MutableList<Pokemon> = pokemonRepository.getStoredPokemons().toMutableList()
        storedPokemons.sortByDescending {
            it.stats?.hp
        }
        return storedPokemons[0].stats?.hp ?: 0
    }

    private fun getExceptionHandler() = CoroutineExceptionHandler { _, exception ->
        Log.e("MyLog","CoroutineExceptionHandler got $exception")
    }
}