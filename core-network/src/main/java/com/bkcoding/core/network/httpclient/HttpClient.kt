package com.bkcoding.core.network.httpclient

import com.bkcoding.core.network.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.net.HttpURLConnection

/**
 * sealed class that represent the network result state
 */
sealed class NetworkResult<T : Any> {
    class Success<T : Any>(
        val data: T,
        val code: Int = HttpURLConnection.HTTP_OK
    ) : NetworkResult<T>()

    class Error<T : Any>(
        val code: Int,
        val message: String?
    ) : NetworkResult<T>()

    class Exception<T : Any>(
        val e: Throwable
    ) : NetworkResult<T>()
}

/**
 * basic http client to execute GET request
 */
class HttpClient private constructor() {

    companion object {
        val shared = HttpClient()
    }

    /**
     * Okhttp instance with interceptors
     * to display logs and add the api key before sending requet
     */
    val client = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                )
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
                    NetworkResult.Success(
                        data = json.decodeFromString<T>(body.string()),
                        code = code
                    )
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
