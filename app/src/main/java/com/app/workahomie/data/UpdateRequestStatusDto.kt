package com.app.workahomie.data

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRequestStatusDto(
    val status: RequestStatus
)
