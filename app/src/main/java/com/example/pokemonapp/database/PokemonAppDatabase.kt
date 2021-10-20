package com.example.pokemonapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokemonapp.database.entity.AppInfoEntity
import com.example.pokemonapp.database.entity.PokemonEntity
import com.example.pokemonapp.database.entity.StatsEntity

@Database(entities = [
    PokemonEntity::class,
    StatsEntity::class,
    AppInfoEntity::class
    ],
    version = 1)
@TypeConverters(Converters::class)
abstract class PokemonAppDatabase: RoomDatabase() {
    abstract val dao: PokemonAppDao

    companion object {
        fun create(applicationContext: Context): PokemonAppDatabase {

            return Room.databaseBuilder(
                applicationContext,
                PokemonAppDatabase::class.java,
                RoomContract.DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()

        }
    }
}


