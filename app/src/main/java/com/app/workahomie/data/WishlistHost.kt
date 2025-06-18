package com.app.workahomie.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WishlistHost(
    @SerialName(value = "_id")
    val id: String,
    val firstName: String,
    val lastName: String,
    val occupation: String,
    val profileImages: List<String>
)