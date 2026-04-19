package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UpdateSessionRequest(
    val name: String? = null,
    val endedAt: String? = null
)