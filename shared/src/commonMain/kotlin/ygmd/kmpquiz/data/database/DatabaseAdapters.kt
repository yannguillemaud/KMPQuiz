package ygmd.kmpquiz.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.database.Quiz_configuration_entity
import ygmd.kmpquiz.database.Quiz_entity
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails

object DatabaseAdapters {
    val quizConfigAdapter =
        Quiz_configuration_entity.Adapter(object : ColumnAdapter<QuizConfigDetails, String> {
            override fun decode(databaseValue: String): QuizConfigDetails =
                Json.decodeFromString(databaseValue)

            override fun encode(value: QuizConfigDetails): String = Json.encodeToString(value)
        })

    val quizAdapter =
        Quiz_entity.Adapter(object : ColumnAdapter<Boolean, String> {
            override fun decode(databaseValue: String): Boolean = databaseValue.toBoolean()
            override fun encode(value: Boolean): String = value.toString()
        })
}