package com.example.routes

import com.example.models.CreateSessionRequest
import com.example.models.ErrorResponse
import com.example.repository.SessionRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.sessionRoutes() {

    val repository = SessionRepository()

    authenticate("auth-jwt") {

        route("/api/sessions") {

            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@post
                }

                val request = call.receive<CreateSessionRequest>()
                val created = repository.create(userId, request.name?.trim())

                call.respond(HttpStatusCode.Created, created)
            }

            get {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@get
                }

                val sessions = repository.getAllByUser(userId)
                call.respond(sessions)
            }

            get("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val sessionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@get
                }

                if (sessionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de sesión inválido"))
                    return@get
                }

                val session = repository.getByIdAndUser(sessionId, userId)

                if (session == null) {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Sesión no encontrada"))
                } else {
                    call.respond(session)
                }
            }

            put("/{id}/close") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val sessionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@put
                }

                if (sessionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de sesión inválido"))
                    return@put
                }

                val updated = repository.closeSession(sessionId, userId)

                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Sesión cerrada"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Sesión no encontrada"))
                }
            }

            delete("/{id}") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                val sessionId = call.parameters["id"]?.toIntOrNull()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@delete
                }

                if (sessionId == null) {
                    call.respond(HttpStatusCode.BadRequest, ErrorResponse("ID de sesión inválido"))
                    return@delete
                }

                val deleted = repository.delete(sessionId, userId)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Sesión eliminada"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Sesión no encontrada"))
                }
            }
        }
    }
}