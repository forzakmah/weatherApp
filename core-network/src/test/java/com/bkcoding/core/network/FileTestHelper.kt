package com.bkcoding.core.network

import java.io.InputStreamReader

object FileTestHelper {
    fun readFileResource(filename: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream(filename)
        val builder = StringBuilder()
        val reader = InputStreamReader(inputStream, "UTF-8")
        reader.readLines().forEach {
            builder.append(it)
        }
        return builder.toString()
    }
}