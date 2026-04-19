package com.example.models

data class EmotionRecord(
    val id: Int,
    val sessionId: Int,
    val label: String,
    val confidence: Float,
    val timestamp: String
)