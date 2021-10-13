package com.example.pokemonapp.ui.pokemon_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.domain.Pokemon
import com.example.pokemonapp.domain.PokemonPreview
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonsListVIewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
): ViewModel() {

    private var mutablePokemonList = MutableLiveData<List<PokemonPreview>>()
    val pokemonList: LiveData<List<PokemonPreview>> = mutablePokemonList

    private var mutablePokemonDetails = MutableLiveData<Pokemon>()
    val pokemonDetails: LiveData<Pokemon> = mutablePokemonDetails

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
}