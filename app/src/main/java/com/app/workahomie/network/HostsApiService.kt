package com.app.workahomie.network

import com.app.workahomie.data.CreateRequestDto
import com.app.workahomie.data.Host
import com.app.workahomie.data.HostsResponse
import com.app.workahomie.data.ListResponse
import com.app.workahomie.data.Request
import com.app.workahomie.data.WishlistDto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://workahomie.vercel.app"

private val json = Json {
    ignoreUnknownKeys = true
}

interface HostsApiService {
    @GET("/hosts/me")
    suspend fun getHostMe(): Host

    @GET("/hosts/wishlisted")
    suspend fun getHosts(
        @Query("offset") offset: Int? = 0,
        @Query("limit") limit: Int? = 10,
        @Query("lat") lat: Double? = null,
        @Query("lng") lng: Double? = null
    ): HostsResponse

    @GET("/wishlists/full")
    suspend fun getWishlist(
        @Query("offset") offset: Int? = 0,
        @Query("limit") limit: Int? = 10,
    ): HostsResponse

    @GET("/requests/incoming")
    suspend fun getIncomingRequests(): ListResponse<Request>

    @GET("/requests/outgoing")
    suspend fun getOutgoingRequests(): ListResponse<Request>

    @POST("/hosts/{id}/requests")
    suspend fun createRequest(
        @Path("id") hostId: String,
        @Body dto: CreateRequestDto
    )

    @POST("/wishlists/add")
    suspend fun addToWishlist(@Body dto: WishlistDto)

    @HTTP(method = "DELETE", path = "/wishlists/remove", hasBody = true)
    suspend fun removeFromWishlist(@Body dto: WishlistDto)

    @Multipart
    @POST("/hosts")
    suspend fun createHost(
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("occupation") occupation: RequestBody,
        @Part("aboutMe") aboutMe: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part profile: MultipartBody.Part? = null
    ): Host

    @Multipart
    @PUT("/hosts/me")
    suspend fun updateHostMe(
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("occupation") occupation: RequestBody,
        @Part("aboutMe") aboutMe: RequestBody,
        @Part("phone") phone: RequestBody?,
        @Part profile: MultipartBody.Part? = null
    ): Host

    @Multipart
    @PUT("hosts/me/place")
    suspend fun updateHostPlace(
        @Part("address") address: RequestBody,
        @Part("placeDescription") placeDescription: RequestBody,
        @Part("placeDetails") placeDetails: RequestBody,
        @Part("facilities") facilities: List<RequestBody>,
        @Part pictures: List<MultipartBody.Part>? = null
    ): Host
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
