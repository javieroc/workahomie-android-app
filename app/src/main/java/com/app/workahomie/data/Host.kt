package com.app.workahomie.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Host(
    @SerialName(value = "_id")
    val id: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val occupation: String,
    val aboutMe: String,
    val profileImages: List<String>,
    val placeDescription: String,
    val placeDetails: String,
    val address: String,
    val location: Location,
    val facilities: List<String>,
    val pictures: List<String>,
    val phone: String? = null,
    var isWishlisted: Boolean? = false
)

@Serializable
data class Location(
    val type: String,
    val coordinates: List<Double>
)
