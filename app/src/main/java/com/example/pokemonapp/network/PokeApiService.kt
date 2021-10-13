package com.example.pokemonapp.network

import com.example.pokemonapp.network.model.pokemon_details.PokemonDetailsDto
import com.example.pokemonapp.network.model.pokemon_list.PokemonListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonListDto

    @GET("pokemon/{pokemonId}")
    suspend fun getPokemonDetailsById(
        @Path("pokemonId") pokemonId: Int
    ): PokemonDetailsDto

    @GET("pokemon/{pokemonName}")
    suspend fun getPokemonDetailsByName(
        @Path("pokemonName") pokemonName: String
    ): PokemonDetailsDto


}