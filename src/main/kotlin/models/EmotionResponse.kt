package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class EmotionResponse(
    val id: Int,
    val sessionId: Int,
    val label: String,
    val confidence: Float,
    val timestamp: String
)