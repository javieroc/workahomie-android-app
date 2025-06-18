package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class WishlistsResponse(
    val data: List<WishlistHost>,
    val total: Int
)
