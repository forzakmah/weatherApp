package com.bkcoding.weather

import android.app.Application
import com.bkcoding.weather.db.AppDatabase

class WeatherApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(context = this) }

    // [WeatherContainer] instance used by the rest of classes to obtain dependencies
    lateinit var container: WeatherContainer
    override fun onCreate() {
        super.onCreate()
        container = WeatherContainerImpl(this)
    }
}