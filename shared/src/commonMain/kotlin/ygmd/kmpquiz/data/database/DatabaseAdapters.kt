package ygmd.kmpquiz.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.database.Quiz_entity
import ygmd.kmpquiz.database.Quiz_scheduler_configuration_entity
import ygmd.kmpquiz.domain.model.cron.SchedulerSelection
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails

object DatabaseAdapters {
    val quizAdapter = Quiz_entity.Adapter(
        questions_configAdapter = object : ColumnAdapter<QuizConfigDetails, String> {
            override fun decode(databaseValue: String): QuizConfigDetails =
                Json.decodeFromString(databaseValue)

            override fun encode(value: QuizConfigDetails): String = Json.encodeToString(value)

        }
    )


    val quizSchedulerConfigurationAdapter =
        Quiz_scheduler_configuration_entity.Adapter(
            selectionAdapter = object : ColumnAdapter<SchedulerSelection, String> {
                override fun decode(databaseValue: String): SchedulerSelection =
                    Json.decodeFromString(databaseValue)

                override fun encode(value: SchedulerSelection): String = Json.encodeToString(value)
            },
            enabledAdapter = object : ColumnAdapter<Boolean, String> {
                override fun decode(databaseValue: String): Boolean = databaseValue.toBoolean()
                override fun encode(value: Boolean): String = value.toString()
            }
        )
}