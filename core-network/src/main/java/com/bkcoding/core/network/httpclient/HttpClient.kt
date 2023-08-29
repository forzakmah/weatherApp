package com.bkcoding.core.network.httpclient

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

sealed class NetworkResult<T : Any> {
    class Success<T : Any>(val data: T) : NetworkResult<T>()
    class Error<T : Any>(val code: Int, val message: String?) : NetworkResult<T>()
    class Exception<T : Any>(val e: Throwable) : NetworkResult<T>()
}

class HttpClient private constructor() {

    companion object {
        val shared = HttpClient()
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        )
        .addInterceptor(
            interceptor = BasicLoggingInterceptor()
        )
        .addInterceptor(
            interceptor = AuthorizationInterceptor()
        )
        .build()

    val json = Json { ignoreUnknownKeys = true }

    inline fun <reified T : Any> get(url: String): NetworkResult<T> {
        return try {
            val request: Request = Request.Builder()
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Content-Type", "application/json")
                .url(url)
                .build()
            val response = client.newCall(request).execute()
            val body = response.body
            val code = response.code
            if (response.isSuccessful) {
                if (body != null)
                    NetworkResult.Success(data = json.decodeFromString<T>(body.string()))
                else
                    NetworkResult.Success(data = "No Content" as T)
            } else {
                NetworkResult.Error(
                    code = code,
                    message = response.message
                )
            }
        } catch (exception: Exception) {
            NetworkResult.Exception(e = exception)
        }
    }
}
