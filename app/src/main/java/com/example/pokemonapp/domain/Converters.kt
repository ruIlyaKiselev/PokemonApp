package com.example.pokemonapp.domain

import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.model.pokemon_details.PokemonDetailsDto
import com.example.pokemonapp.network.model.pokemon_list.PokemonListDto

class Converters {
    companion object {
        fun PokemonDetailsDto.toDomain(): Pokemon = Pokemon(
            id = this.id,
            pokemonName = this.name,
            imageUrl = this.sprites?.frontDefault,
            height = this.height,
            weight = this.weight,
            type = this.types?.map {
                it.type?.name
            } ?: emptyList(),
            stats = Stats(
                hp = this.stats?.get(0)?.baseStat,
                attack = this.stats?.get(1)?.baseStat,
                defence = this.stats?.get(2)?.baseStat,
                specialAttack = this.stats?.get(3)?.baseStat,
                specialDefence = this.stats?.get(4)?.baseStat,
                speed = this.stats?.get(5)?.baseStat
            )
        )

        fun PokemonListDto.toDomain(): List<PokemonPreview> = this.results?.map {
            val parsedId = it.url?.substringAfter("pokemon/")?.dropLast(1)?.toInt()
            PokemonPreview(
                id = parsedId,
                pokemonName = it.name,
                imageUrl = "${PokeApiContract.IMAGES_URL}$parsedId.png",
                loadedFullInfo = false
            )
        } ?: emptyList()
    }
}