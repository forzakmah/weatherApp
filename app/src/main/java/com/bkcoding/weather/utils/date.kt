package com.bkcoding.weather.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date


@SuppressLint("SimpleDateFormat")
fun Long.toTime(): String {
    val date = Date(this * 1000)
    val format = SimpleDateFormat("HH:mm")
    return format.format(date)
}
