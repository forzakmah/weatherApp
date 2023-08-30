package com.bkcoding.weather.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherInfoDao {
    @Query("SELECT * FROM weather_info")
    fun getAllWeathers(): Flow<List<WeatherInfoEntity>>

    @Query("SELECT * FROM weather_info WHERE id IN (:ids)")
    fun weathersInfoByIds(ids: List<Long>): Flow<List<WeatherInfoEntity>>

    @Query("SELECT * FROM weather_info WHERE id = :id")
    fun weatherById(id: Long): Flow<WeatherInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weather: WeatherInfoEntity): Long

    @Delete
    suspend fun delete(weather: WeatherInfoEntity)

    @Update
    fun updateCity(weather: WeatherInfoEntity)
}