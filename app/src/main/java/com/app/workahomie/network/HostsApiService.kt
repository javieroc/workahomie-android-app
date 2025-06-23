package com.app.workahomie.network

import com.app.workahomie.data.HostsResponse
import com.app.workahomie.data.WishlistDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

private const val BASE_URL = "https://workahomie.vercel.app"

private val json = Json {
    ignoreUnknownKeys = true
}

interface HostsApiService {
    @GET("/hosts/wishlisted")
    suspend fun getHosts(
        @Query("offset") offset: Int? = 0,
        @Query("limit") limit: Int? = 10
    ): HostsResponse

    @GET("/wishlists/full")
    suspend fun getWishlist(
        @Query("offset") offset: Int? = 0,
        @Query("limit") limit: Int? = 10
    ): HostsResponse

    @POST("/wishlists/add")
    suspend fun addToWishlist(@Body dto: WishlistDto)

    @HTTP(method = "DELETE", path = "/wishlists/remove", hasBody = true)
    suspend fun removeFromWishlist(@Body dto: WishlistDto)
}


object HostApi {
    @Volatile private var accessToken: String? = null

    fun setToken(token: String) {
        accessToken = token
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthTokenInterceptor { accessToken })
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val retrofitService: HostsApiService by lazy {
        retrofit.create(HostsApiService::class.java)
    }
}
