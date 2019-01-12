package com.github.khronos227.syncedscrolllist.util

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttp {
    val client: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(60_000, TimeUnit.MILLISECONDS)
        .readTimeout(60_000, TimeUnit.MILLISECONDS)
        .writeTimeout(60_000, TimeUnit.MILLISECONDS)
        .build()
}
