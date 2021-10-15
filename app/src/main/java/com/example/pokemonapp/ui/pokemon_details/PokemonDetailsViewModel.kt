package com.example.pokemonapp.ui.pokemon_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.network.PokeApiPageSource
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
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("MyLog","CoroutineExceptionHandler got $exception")
        }

        viewModelScope.launch(Dispatchers.IO + handler) {
            mutablePokemonDetails.postValue(
                pokemonRepository.loadPokemonDetailsById(pokemonId)
            )
        }
    }
}