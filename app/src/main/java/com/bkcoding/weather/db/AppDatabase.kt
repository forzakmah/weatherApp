package com.bkcoding.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bkcoding.weather.db.entity.WeatherInfoEntity
import com.bkcoding.weather.db.dao.WeatherInfoDao

const val databaseName = "weather_app_database"

@Database(
    entities = [
        WeatherInfoEntity::class
    ],
    version = 1
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    databaseName
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }

}