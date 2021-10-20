package com.example.pokemonapp.database

import android.provider.BaseColumns

object RoomContract {
    const val DATABASE_NAME = "PokemonApp.db"

    object Pokemon {
        const val TABLE_NAME = "Pokemon"

        const val COLUMN_NAME_ID = BaseColumns._ID // This is id for database (autogenerate); I Use it for paging source, that needs id's ordering
        const val COLUMN_NAME_POKEMON_ID = "PokemonId" // This is id from pokemons API (sometimes it loses order, for example last elements have 10xxx id instead of 1***
        const val COLUMN_NAME_POKEMON_NAME = "PokemonName"
        const val COLUMN_NAME_IMAGE_URL = "ImageUrl"
        const val COLUMN_NAME_HEIGHT = "Height"
        const val COLUMN_NAME_WEIGHT = "Weight"
        const val COLUMN_NAME_STATS = "Stats"
        const val COLUMN_NAME_TYPE = "Type"
    }

    object Stats {
        const val TABLE_NAME = "Stats"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_HP = "Hp"
        const val COLUMN_NAME_ATTACK = "Attack"
        const val COLUMN_NAME_DEFENCE = "Defence"
        const val COLUMN_NAME_SPECIAL_ATTACK = "SpecialAttack"
        const val COLUMN_NAME_SPECIAL_DEFENCE = "SpecialDefence"
        const val COLUMN_NAME_SPEED = "Speed"
    }

    object AppInfo {
        const val TABLE_NAME = "AppInfo"

        const val COLUMN_NAME_ID = BaseColumns._ID
        const val COLUMN_NAME_LAST_UPDATE = "LastUpdate"
        const val COLUMN_NAME_COUNT_OF_ELEMENTS = "CountOfElements"
    }

}