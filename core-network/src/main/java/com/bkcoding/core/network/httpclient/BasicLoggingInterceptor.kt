package com.bkcoding.core.network.httpclient

import android.util.Log
import com.bkcoding.core.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class BasicLoggingInterceptor : Interceptor {
    companion object {
        val TAG = BasicLoggingInterceptor::class.simpleName
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (BuildConfig.DEBUG) Log.i(TAG, request.url.toString())
        return chain.proceed(request)
    }
}