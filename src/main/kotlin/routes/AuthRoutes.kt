package com.example.routes

import com.example.models.ErrorResponse
import com.example.models.LoginRequest
import com.example.models.LoginResponse
import com.example.models.RegisterRequest
import com.example.repository.UserRepository
import com.example.security.JwtConfig
import com.example.security.PasswordUtils
import com.example.security.ValidationUtils
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    val repository = UserRepository()

    route("/api/auth") {

        post("/register") {
            val request = call.receive<RegisterRequest>()
            val email = request.email.trim().lowercase()
            val password = request.password.trim()

            if (email.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El email es obligatorio"))
                return@post
            }

            if (!ValidationUtils.isValidEmail(email)) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Formato de email inválido"))
                return@post
            }

            if (password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La contraseña es obligatoria"))
                return@post
            }

            if (password.length < 8) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La contraseña debe tener al menos 8 caracteres"))
                return@post
            }

            val existingUser = repository.findByEmail(email)
            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("El usuario ya existe"))
                return@post
            }

            val passwordHash = PasswordUtils.hash(password)
            val createdUser = repository.create(email, passwordHash)

            call.respond(HttpStatusCode.Created, createdUser)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val email = request.email.trim().lowercase()
            val password = request.password.trim()

            if (email.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("El email es obligatorio"))
                return@post
            }

            if (password.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("La contraseña es obligatoria"))
                return@post
            }

            val user = repository.findByEmail(email)
            if (user == null || !PasswordUtils.verify(password, user.passwordHash)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Credenciales inválidas"))
                return@post
            }

            val token = JwtConfig.generateToken(user.id, user.email)

            call.respond(
                HttpStatusCode.OK,
                LoginResponse(
                    token = token,
                    userId = user.id,
                    email = user.email
                )
            )
        }

        authenticate("auth-jwt") {
            delete("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Token inválido"))
                    return@delete
                }

                val deleted = repository.deleteById(userId)

                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Usuario eliminado correctamente"))
                } else {
                    call.respond(HttpStatusCode.NotFound, ErrorResponse("Usuario no encontrado"))
                }
            }
        }
    }
}