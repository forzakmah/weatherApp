package com.bkcoding.core.network.httpclient

import com.bkcoding.core.network.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {
    companion object {
        const val API_KEY = "appid"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val newUrl: HttpUrl = chain.request().url
            .newBuilder()
            .addQueryParameter(
                name = API_KEY,
                value = BuildConfig.OPEN_WEATHER_API_KEY
            )
            .build()

        val newRequest: Request = chain.request()
            .newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }
}