package com.example.repository

import com.example.database.Emotions
import com.example.database.Sessions
import com.example.models.EmotionResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EmotionRepository {

    fun create(userId: Int, sessionId: Int, label: String, confidence: Float): EmotionResponse? {
        return transaction {
            val validSession = Sessions.selectAll()
                .where { (Sessions.id eq sessionId) and (Sessions.userId eq userId) }
                .singleOrNull()

            if (validSession == null) return@transaction null

            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

            val emotionId = Emotions.insert {
                it[Emotions.sessionId] = sessionId
                it[Emotions.label] = label
                it[Emotions.confidence] = confidence
                it[Emotions.timestamp] = now
            } get Emotions.id

            EmotionResponse(
                id = emotionId,
                sessionId = sessionId,
                label = label,
                confidence = confidence,
                timestamp = now
            )
        }
    }

    fun getAllByUser(userId: Int): List<EmotionResponse> {
        return transaction {
            (Emotions innerJoin Sessions)
                .selectAll()
                .where { Sessions.userId eq userId }
                .map {
                    EmotionResponse(
                        id = it[Emotions.id],
                        sessionId = it[Emotions.sessionId],
                        label = it[Emotions.label],
                        confidence = it[Emotions.confidence],
                        timestamp = it[Emotions.timestamp]
                    )
                }
        }
    }

    fun getByIdAndUser(emotionId: Int, userId: Int): EmotionResponse? {
        return transaction {
            (Emotions innerJoin Sessions)
                .selectAll()
                .where { (Emotions.id eq emotionId) and (Sessions.userId eq userId) }
                .map {
                    EmotionResponse(
                        id = it[Emotions.id],
                        sessionId = it[Emotions.sessionId],
                        label = it[Emotions.label],
                        confidence = it[Emotions.confidence],
                        timestamp = it[Emotions.timestamp]
                    )
                }
                .singleOrNull()
        }
    }

    fun getBySessionAndUser(sessionId: Int, userId: Int): List<EmotionResponse> {
        return transaction {
            (Emotions innerJoin Sessions)
                .selectAll()
                .where { (Emotions.sessionId eq sessionId) and (Sessions.userId eq userId) }
                .map {
                    EmotionResponse(
                        id = it[Emotions.id],
                        sessionId = it[Emotions.sessionId],
                        label = it[Emotions.label],
                        confidence = it[Emotions.confidence],
                        timestamp = it[Emotions.timestamp]
                    )
                }
        }
    }

    fun update(emotionId: Int, userId: Int, sessionId: Int, label: String, confidence: Float): EmotionResponse? {
        return transaction {
            val validSession = Sessions.selectAll()
                .where { (Sessions.id eq sessionId) and (Sessions.userId eq userId) }
                .singleOrNull()

            if (validSession == null) return@transaction null

            val ownedEmotion = (Emotions innerJoin Sessions)
                .selectAll()
                .where { (Emotions.id eq emotionId) and (Sessions.userId eq userId) }
                .singleOrNull()

            if (ownedEmotion == null) return@transaction null

            Emotions.update({ Emotions.id eq emotionId }) {
                it[Emotions.sessionId] = sessionId
                it[Emotions.label] = label
                it[Emotions.confidence] = confidence
            }

            Emotions.selectAll()
                .where { Emotions.id eq emotionId }
                .map {
                    EmotionResponse(
                        id = it[Emotions.id],
                        sessionId = it[Emotions.sessionId],
                        label = it[Emotions.label],
                        confidence = it[Emotions.confidence],
                        timestamp = it[Emotions.timestamp]
                    )
                }
                .singleOrNull()
        }
    }

    fun delete(emotionId: Int, userId: Int): Boolean {
        return transaction {
            val ownedEmotion = (Emotions innerJoin Sessions)
                .selectAll()
                .where { (Emotions.id eq emotionId) and (Sessions.userId eq userId) }
                .singleOrNull()

            if (ownedEmotion == null) return@transaction false

            Emotions.deleteWhere { Emotions.id eq emotionId } > 0
        }
    }
}