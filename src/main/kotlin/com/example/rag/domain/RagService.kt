package com.example.rag.domain

import com.example.rag.infrastructure.VectorStoreProvider
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.service.AiServices
import dev.langchain4j.service.SystemMessage
import dev.langchain4j.service.UserMessage
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import java.time.Duration

interface Assistant {
    @SystemMessage("""
        Eres un asistente experto en salud emocional para la aplicación EmotionAI.
        Tu objetivo es ayudar al usuario basándote únicamente en la información proporcionada en el contexto.
        Si no sabes la respuesta basándote en el contexto, dilo amablemente.
        Responde siempre en español.
    """)
    fun chat(userMessage: String): String
}

class RagService {
    private val embeddingModel = AllMiniLmL6V2EmbeddingModel()
    private val chatModel = OllamaChatModel.builder()
        .baseUrl(System.getenv("OLLAMA_HOST") ?: "http://localhost:11434")
        .modelName("phi3")
        .timeout(Duration.ofMinutes(2))
        .build()

    private val assistant: Assistant by lazy {
        val contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingStore(VectorStoreProvider.store)
            .embeddingModel(embeddingModel)
            .maxResults(3)
            .minScore(0.6)
            .build()

        AiServices.builder(Assistant::class.java)
            .chatLanguageModel(chatModel)
            .contentRetriever(contentRetriever)
            .build()
    }

    fun ask(question: String): String {
        return try {
            assistant.chat(question)
        } catch (e: Exception) {
            "Lo siento, tuve un problema procesando tu consulta: ${e.message}"
        }
    }

    fun ingestData(text: String, userId: Int? = null) {
        val ingestor = EmbeddingStoreIngestor.builder()
            .embeddingStore(VectorStoreProvider.store)
            .embeddingModel(embeddingModel)
            .build()
        
        val document = if (userId != null) {
            Document.from(text, dev.langchain4j.data.document.Metadata.from("userId", userId.toString()))
        } else {
            Document.from(text)
        }
        
        ingestor.ingest(document)
    }
}
