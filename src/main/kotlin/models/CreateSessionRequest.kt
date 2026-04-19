package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionRequest(
    val name: String? = null
)