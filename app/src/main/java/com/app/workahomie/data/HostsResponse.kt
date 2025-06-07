package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class HostsResponse(
    val data: List<Host>,
    val total: Int
)
