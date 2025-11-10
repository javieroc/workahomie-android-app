package com.app.workahomie.data

data class HostFilters(
    val lat: Double? = null,
    val lng: Double? = null,
    val occupations: List<String> = emptyList(),
    val facilities: List<String> = emptyList(),
    val rate: Double? = null
)
