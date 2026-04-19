package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class EmotionRequest(
    val sessionId: Int,
    val label: String,
    val confidence: Float
)