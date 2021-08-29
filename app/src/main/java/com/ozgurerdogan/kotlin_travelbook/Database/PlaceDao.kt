package com.ozgurerdogan.kotlin_travelbook.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaceDao {
    @Query("SELECT * FROM place")
    fun getAll(): List<Place>

    @Insert
    fun insertAll(vararg place: Place)

    @Delete
    fun delete(place: Place)
}