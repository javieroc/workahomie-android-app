package com.app.workahomie.network

import com.app.workahomie.data.HostsResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET

private const val BASE_URL = "https://workahomie.vercel.app"

private val json = Json {
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface HostsApiService {
    @GET("hosts")
    suspend fun getHosts(): HostsResponse
}

object HostApi {
    val retrofitService: HostsApiService by lazy {
        retrofit.create(HostsApiService::class.java)
    }
}
