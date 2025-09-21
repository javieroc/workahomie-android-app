package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class ListResponse<T>(
    val data: List<T>,
    val total: Int
)
