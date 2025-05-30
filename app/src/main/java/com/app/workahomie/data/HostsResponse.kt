package com.app.workahomie.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HostsResponse(
    @SerialName("data")
    val data: List<Host>
)
