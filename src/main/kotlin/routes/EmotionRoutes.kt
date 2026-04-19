package com.example.routes

import com.example.models.EmotionRequest
import com.example.models.ErrorResponse
import com.example.repository.EmotionRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.emotionRoutes() {

    val repository = EmotionRepository()

    get("/health") {
        call.respond(mapOf("status" to "OK"))
    }

    authenticate("auth-jwt") {
        route("/api/emotions") {

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@post
                }

                val request = call.receive<EmotionRequest>()

                val created = repository.create(
                    userId = userId,
                    sessionId = request.sessionId,
                    label = request.label,
                    confidence = request.confidence
                )

                if (created == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("Sesión no válida para este usuario"))
                } else {
                    call.respond(HttpStatusCode.Created, created)
                }
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@get
                }

                call.respond(repository.getAllByUser(userId))
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val emotionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@get
                }

                if (emotionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                    return@get
                }

                val emotion = repository.getByIdAndUser(emotionId, userId)
                if (emotion == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Emoción no encontrada"))
                } else {
                    call.respond(emotion)
                }
            }

            get("/by-session/{sessionId}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val sessionId = call.parameters["sessionId"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@get
                }

                if (sessionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de sesión inválido"))
                    return@get
                }

                call.respond(repository.getBySessionAndUser(sessionId, userId))
            }

            put("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val emotionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@put
                }

                if (emotionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                    return@put
                }

                val request = call.receive<EmotionRequest>()

                val updated = repository.update(
                    emotionId = emotionId,
                    userId = userId,
                    sessionId = request.sessionId,
                    label = request.label,
                    confidence = request.confidence
                )

                if (updated == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Emoción no encontrada o sesión inválida"))
                } else {
                    call.respond(updated)
                }
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val emotionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@delete
                }

                if (emotionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID inválido"))
                    return@delete
                }

                val deleted = repository.delete(emotionId, userId)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Emoción eliminada"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Emoción no encontrada"))
                }
            }
        }
    }
}