plugins {
    kotlin("jvm") version "2.1.0"
    id("io.ktor.plugin") version "3.0.3"
    kotlin("plugin.serialization") version "2.1.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "com.example.ApplicationKt"
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")

    // Base de datos
    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.46.0")
    implementation("org.postgresql:postgresql:42.7.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("org.mindrot:jbcrypt:0.4")

    implementation("io.ktor:ktor-server-auth-jvm:2.3.12")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:2.3.12")

    // RAG & LLM
    implementation("dev.langchain4j:langchain4j:0.31.0")
    implementation("dev.langchain4j:langchain4j-ollama:0.31.0")
    implementation("dev.langchain4j:langchain4j-pgvector:0.31.0")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:0.31.0")
}
