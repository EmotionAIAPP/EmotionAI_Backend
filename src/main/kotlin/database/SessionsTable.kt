package com.example.database

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Sessions : Table("sessions") {
    val id = integer("id").autoIncrement()
    val userId = reference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 100).nullable()
    val startedAt = varchar("started_at", 50)
    val endedAt = varchar("ended_at", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}