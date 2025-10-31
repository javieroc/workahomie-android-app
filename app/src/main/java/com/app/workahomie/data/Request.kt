package com.app.workahomie.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RequestStatus {
    accepted,
    declined,
    pending
}

@Serializable
data class Request(
    @SerialName("_id")
    val id: String,
    val userId: String? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val userEmail: String? = null,
    val checkIn: String,
    val checkOut: String,
    val status: RequestStatus? = null,
    val host: Host,
    val messages: List<String>
)
