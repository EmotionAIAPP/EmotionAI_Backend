package com.example.models

data class UserRecord(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val createdAt: String
)