package com.ozgurerdogan.kotlin_travelbook.Database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Place::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}