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

@SuppressLint("SimpleDateFormat")
fun Long.formatTo(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val date = Date(this)
    val simpleDateFormat = SimpleDateFormat(pattern)
    return simpleDateFormat.format(date)
}
