package com.example.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val url = System.getenv("DB_URL") ?: "jdbc:postgresql://db:5432/emotionai"
        val user = System.getenv("DB_USER") ?: "postgres"
        val password = System.getenv("DB_PASSWORD") ?: "password123"

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(Users, Sessions, Emotions)
        }

        println("Base de datos conectada y tablas creadas")
    }
}