package com.example.pokemonapp.network

import com.example.pokemonapp.network.model.pokemons_list.PokemonsListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonsList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonsListDto

    @GET("pokemon/{pokemonId}")
    suspend fun getPokemonDetailsById(
        @Path("pokemonId") pokemonId: Int
    )


}