package com.bkcoding.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bkcoding.weather.db.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM city")
    fun getAllCities(): Flow<List<CityEntity>>

    @Query("SELECT * FROM city WHERE id IN (:ids)")
    fun citiesByIds(ids: List<Long>): Flow<List<CityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg cities: CityEntity): List<Long>

    @Delete
    suspend fun delete(cityEntity: CityEntity)
}