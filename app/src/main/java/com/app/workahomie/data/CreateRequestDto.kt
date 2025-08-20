package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class CreateRequestDto(
    val checkIn: String,
    val checkOut: String,
    val message: String? = null,
    val userName: String? = null,
    val userEmail: String? = null,
    val userAvatar: String? = null
)
