package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val text: String,
    val senderId: String,
    val timestamp: String
)
