package com.example

import com.example.database.DatabaseFactory
import com.example.rag.domain.RagService
import com.example.rag.web.ragRouting
import com.example.routes.authRoutes
import com.example.routes.emotionRoutes
import com.example.routes.sessionRoutes
import com.example.security.configureSecurity
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }

        configureSecurity()
        DatabaseFactory.init()

        val ragService = RagService()

        routing {
            authRoutes()
            sessionRoutes()
            emotionRoutes(ragService)
            ragRouting(ragService)
        }
    }.start(wait = true)
}