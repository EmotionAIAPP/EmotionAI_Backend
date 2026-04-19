package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private const val secret = "emotionai_super_secret_key_2026"
    private const val issuer = "emotionai-backend"
    private const val audience = "emotionai-app"
    private const val validityInMs = 36_000_00 * 24 // 24 horas

    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: Int, email: String): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    fun getSecret() = secret
    fun getIssuer() = issuer
    fun getAudience() = audience
    fun getAlgorithm() = algorithm
}