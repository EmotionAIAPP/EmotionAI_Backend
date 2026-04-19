package com.example.security

object ValidationUtils {
    private val emailRegex =
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun normalizeEmail(email: String): String {
        return email.trim().lowercase()
    }

    fun isValidEmail(email: String): Boolean {
        return emailRegex.matches(normalizeEmail(email))
    }
}