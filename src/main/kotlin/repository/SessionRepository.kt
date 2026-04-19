package com.example.repository

import com.example.database.Sessions
import com.example.models.SessionRecord
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SessionRepository {

    private fun rowToSession(row: ResultRow): SessionRecord {
        return SessionRecord(
            id = row[Sessions.id],
            userId = row[Sessions.userId],
            name = row[Sessions.name],
            startedAt = row[Sessions.startedAt],
            endedAt = row[Sessions.endedAt]
        )
    }

    fun create(userId: Int, name: String?): SessionRecord {
        return transaction {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val id = Sessions.insert {
                it[Sessions.userId] = userId
                it[Sessions.name] = name
                it[Sessions.startedAt] = now
                it[Sessions.endedAt] = null
            } get Sessions.id

            SessionRecord(
                id = id,
                userId = userId,
                name = name,
                startedAt = now,
                endedAt = null
            )
        }
    }

    fun getAllByUser(userId: Int): List<SessionRecord> {
        return transaction {
            Sessions.selectAll()
                .where { Sessions.userId eq userId }
                .map { rowToSession(it) }
        }
    }

    fun getByIdAndUser(sessionId: Int, userId: Int): SessionRecord? {
        return transaction {
            Sessions.selectAll()
                .where { (Sessions.id eq sessionId) and (Sessions.userId eq userId) }
                .map { rowToSession(it) }
                .singleOrNull()
        }
    }

    fun closeSession(sessionId: Int, userId: Int): Boolean {
        return transaction {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            Sessions.update({
                (Sessions.id eq sessionId) and (Sessions.userId eq userId)
            }) {
                it[endedAt] = now
            } > 0
        }
    }

    fun delete(sessionId: Int, userId: Int): Boolean {
        return transaction {
            Sessions.deleteWhere {
                (Sessions.id eq sessionId) and (Sessions.userId eq userId)
            } > 0
        }
    }
}