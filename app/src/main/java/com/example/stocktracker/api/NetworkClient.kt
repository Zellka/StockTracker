package com.example.stocktracker.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.*

object NetworkClient {
    private const val BASE_URL = "https://financialmodelingprep.com/api/v3/"
    private const val cacheSize = (10 * 1024 * 1024).toLong()
    private var retrofit: Retrofit? = null

    fun getClient(context: Context, hasInternet: Boolean): Retrofit {
        if (retrofit == null) {
            val cache = Cache(context.cacheDir, cacheSize)
            val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (hasInternet)
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                    else
                        request.newBuilder().header(
                            "Cache-Control",
                            "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                        ).build()
                    chain.proceed(request)
                }

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient.build())
                .build()
        }
        return retrofit!!
    }
}