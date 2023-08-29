package com.bkcoding.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkCity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val state: String,
    val country: String
)