package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class SessionRecord(
    val id: Int = 0,
    val userId: Int,
    val name: String? = null,
    val startedAt: String,
    val endedAt: String? = null
)