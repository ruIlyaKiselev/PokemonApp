package com.example.pokemonapp.network

/*
*
*       This is contract of network
*       I don't like hardcode network info (api key and stuff like that) and save it in special class
*
* */
object PokeApiContract {
    const val BASE_URL = "https://pokeapi.co/api/v2/"
    const val ITEMS_PER_PAGE = 30
    const val IMAGES_URL = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
}