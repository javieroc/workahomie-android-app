package com.app.workahomie.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Host(
    @SerialName(value = "_id")
    val id: String = "",
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val occupation: String = "",
    val aboutMe: String = "",
    val profileImages: List<String> = emptyList(),
    val placeDescription: String = "",
    val placeDetails: String = "",
    val address: String = "",
    val location: Location? = null, // optional, since new hosts may not have one yet
    val facilities: List<String> = emptyList(),
    val pictures: List<String> = emptyList(),
    val phone: String? = null,
    val rate: Float = 0f,
    val countReviews: Int = 0,
    var isWishlisted: Boolean? = false
)

@Serializable
data class Location(
    val type: String = "Point",
    val coordinates: List<Double> = listOf(0.0, 0.0)
)
