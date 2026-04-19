package com.example.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Emotions : Table("emotions") {
    val id = integer("id").autoIncrement()
    val sessionId = reference("session_id", Sessions.id, onDelete = ReferenceOption.CASCADE)
    val label = varchar("label", 50)
    val confidence = float("confidence")
    val timestamp = varchar("timestamp", 50)

    override val primaryKey = PrimaryKey(id)
}