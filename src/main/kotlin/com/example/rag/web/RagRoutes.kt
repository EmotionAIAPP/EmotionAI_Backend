package com.example.rag.web

import com.example.rag.domain.RagService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RagRequest(val message: String)

@Serializable
data class RagResponse(val answer: String)

fun Route.ragRouting(ragService: RagService) {
    route("/rag") {
        post("/ask") {
            val request = call.receive<RagRequest>()
            val answer = ragService.ask(request.message)
            call.respond(RagResponse(answer))
        }

        post("/ingest") {
            val request = call.receive<RagRequest>()
            ragService.ingestData(request.message)
            call.respond(mapOf("status" to "success", "message" to "Data ingested successfully"))
        }
    }
}
