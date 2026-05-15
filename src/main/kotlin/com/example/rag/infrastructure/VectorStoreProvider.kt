package com.example.rag.infrastructure

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore
import java.sql.DriverManager

object VectorStoreProvider {
    private val host = System.getenv("DB_HOST") ?: "db"
    private val port = 5432
    private val database = "emotionai"
    private val user = "postgres"
    private val password = "admin123"

    val store: EmbeddingStore<TextSegment> by lazy {
        PgVectorEmbeddingStore.builder()
            .host(host)
            .port(port)
            .database(database)
            .user(user)
            .password(password)
            .table("embeddings")
            .dimension(384) // Dimensión para all-minilm-l6-v2
            .build()
    }
}
