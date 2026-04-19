package com.example.security

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "emotionai"
            verifier(
                com.auth0.jwt.JWT
                    .require(JwtConfig.getAlgorithm())
                    .withAudience(JwtConfig.getAudience())
                    .withIssuer(JwtConfig.getIssuer())
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                if (userId != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}