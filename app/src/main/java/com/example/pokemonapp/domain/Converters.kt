package com.example.pokemonapp.domain

import com.example.pokemonapp.database.entity.PokemonEntity
import com.example.pokemonapp.database.entity.StatsEntity
import com.example.pokemonapp.network.PokeApiContract
import com.example.pokemonapp.network.model.pokemon_details.PokemonDetailsDto
import com.example.pokemonapp.network.model.pokemon_list.PokemonListDto

object Converters {

    fun PokemonDetailsDto.toDomain(): Pokemon {

        val result = Pokemon(
            id = this.id,
            pokemonName = this.name,
            imageUrl = this.sprites?.frontDefault
        )

        result.height = this.height
        result.weight = this.weight
        result.type = this.types?.map {
            it.type?.name
        } ?: emptyList()
        result.stats = Stats(
            hp = this.stats?.get(0)?.baseStat,
            attack = this.stats?.get(1)?.baseStat,
            defence = this.stats?.get(2)?.baseStat,
            specialAttack = this.stats?.get(3)?.baseStat,
            specialDefence = this.stats?.get(4)?.baseStat,
            speed = this.stats?.get(5)?.baseStat
        )

        return result
    }


    fun PokemonListDto.toDomain(): List<PokemonPreview> = this.results?.map {
        val parsedId = it.url?.substringAfter("pokemon/")?.dropLast(1)?.toInt()
        PokemonPreview(
            id = parsedId,
            pokemonName = it.name,
            imageUrl = "${PokeApiContract.IMAGES_URL}$parsedId.png"
        )
    } ?: emptyList()

    fun PokemonListDto.toPreloaded(): List<Pokemon> {
        val pokemonPreviewList = this.toDomain()
        return pokemonPreviewList.map {
            Pokemon(
                id = it.id,
                pokemonName = it.pokemonName,
                imageUrl = it.imageUrl
            )
        }
    }

    fun Stats.toEntity(): StatsEntity = StatsEntity(
        hp = hp ?: 0,
        attack = attack ?: 0,
        defence = defence ?: 0,
        specialAttack = specialAttack ?: 0,
        specialDefence = specialDefence ?: 0,
        speed = speed ?: 0
    )

    fun Pokemon.toEntity(): PokemonEntity = PokemonEntity(
        id = id ?: 0,
        pokemonName = pokemonName ?: "",
        imageUrl = imageUrl ?: "",
        height = height ?: 0,
        weight = weight ?: 0,
        type = type,
        stats = stats!!.toEntity()
    )

    fun StatsEntity.toDomain(): Stats = Stats(
        hp, attack, defence, specialAttack, specialDefence, speed
    )

    fun PokemonEntity.toDomain(): Pokemon {
        val result = Pokemon(
            id = id,
            pokemonName = pokemonName,
            imageUrl = imageUrl
        )

        result.height = this.height
        result.weight = this.weight
        result.stats = this.stats.toDomain()
        result.type = this.type

        return result
    }

    fun Pokemon.toPreview(): PokemonPreview = PokemonPreview(
        id, pokemonName, imageUrl
    )

}