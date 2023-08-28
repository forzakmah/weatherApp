package com.bkcoding.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NetworkCity(
    val name: String,
    val lat: Double,
    val long: Double
)
