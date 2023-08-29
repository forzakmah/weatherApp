package com.bkcoding.weather

import android.app.Application
import com.bkcoding.weather.db.AppDatabase

class WeatherApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(context = this) }
}