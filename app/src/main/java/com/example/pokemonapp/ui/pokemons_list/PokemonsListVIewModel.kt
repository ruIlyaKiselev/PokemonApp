package com.example.pokemonapp.ui.pokemons_list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.PokeApiService
import com.example.pokemonapp.network.model.pokemons_list.PokemonsListDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonsListVIewModel @Inject constructor(
    private val pokeApiService: PokeApiService
): ViewModel() {

    private var mutablePokemons = MutableLiveData<PokemonsListDto>()
    val pokemons: LiveData<PokemonsListDto> = mutablePokemons

    fun loadPokemonsList() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("MyLog","CoroutineExceptionHandler got $exception")
        }

        viewModelScope.launch(Dispatchers.IO) {
            mutablePokemons.postValue(
                pokeApiService.getPokemonsList(
                    PokeApiContract.ITEMS_PER_PAGE,
                    0
                )
            )
        }
    }
}