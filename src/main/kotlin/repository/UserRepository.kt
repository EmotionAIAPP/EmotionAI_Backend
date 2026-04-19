package com.example.repository

import com.example.database.Users
import com.example.models.UserRecord
import com.example.models.UserResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class UserRepository {

    private fun rowToUserRecord(row: ResultRow): UserRecord {
        return UserRecord(
            id = row[Users.id],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            createdAt = row[Users.createdAt]
        )
    }

    fun findByEmail(email: String): UserRecord? {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email }
                .map { rowToUserRecord(it) }
                .singleOrNull()
        }
    }

    fun create(email: String, passwordHash: String): UserResponse {
        return transaction {
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val id = Users.insert {
                it[Users.email] = email
                it[Users.passwordHash] = passwordHash
                it[Users.createdAt] = now
            } get Users.id

            UserResponse(
                id = id,
                email = email,
                createdAt = now
            )
        }
    }

    fun deleteById(userId: Int): Boolean {
        return transaction {
            Users.deleteWhere { Users.id eq userId } > 0
        }
    }
}