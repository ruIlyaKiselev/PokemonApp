package com.example.pokemonapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokemonapp.database.RoomContract

@Entity(tableName = RoomContract.AppInfo.TABLE_NAME)
data class AppInfoEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = RoomContract.Pokemon.COLUMN_NAME_ID)
    val _id: Int,
    @ColumnInfo(name = RoomContract.AppInfo.COLUMN_NAME_LAST_UPDATE)
    val lastUpdate: String = "",
    @ColumnInfo(name = RoomContract.AppInfo.COLUMN_NAME_COUNT_OF_ELEMENTS)
    val countOfElements: Int? = 0,
) {
    constructor(
        lastUpdate: String,
        countOfElements: Int
    ) : this(
        _id = 0,
        lastUpdate = lastUpdate,
        countOfElements = countOfElements
    )
}