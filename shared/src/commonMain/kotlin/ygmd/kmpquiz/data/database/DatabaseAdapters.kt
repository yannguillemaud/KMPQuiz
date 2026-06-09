package ygmd.kmpquiz.data.database

import app.cash.sqldelight.ColumnAdapter
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.database.Qanda_entity
import ygmd.kmpquiz.database.Quiz_entity
import ygmd.kmpquiz.database.Quiz_scheduler_configuration_entity
import ygmd.kmpquiz.database.Quiz_session
import ygmd.kmpquiz.core.domain.quiz.config.SchedulerSelection
import ygmd.kmpquiz.core.domain.qanda.Answers
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.domain.quiz.config.QuizQuestionsConfiguration
import ygmd.kmpquiz.core.domain.session.Session

object DatabaseAdapters {
    val quizAdapter = Quiz_entity.Adapter(
        questions_configAdapter = object : ColumnAdapter<QuizQuestionsConfiguration, String> {
            override fun decode(databaseValue: String): QuizQuestionsConfiguration =
                Json.decodeFromString(databaseValue)
            override fun encode(value: QuizQuestionsConfiguration): String = Json.encodeToString(value)
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

    val qandaAdapter = Qanda_entity.Adapter(
        questionAdapter = object : ColumnAdapter<QuestionContent, String> {
            override fun decode(databaseValue: String): QuestionContent =
                Json.decodeFromString(databaseValue)
            override fun encode(value: QuestionContent): String = Json.encodeToString(value)
        },
        answersAdapter = object : ColumnAdapter<Answers, String> {
            override fun decode(databaseValue: String): Answers =
                Json.decodeFromString(databaseValue)
            override fun encode(value: Answers): String = Json.encodeToString(value)
        }
    )

    val sessionAdapter = Quiz_session.Adapter(
        stateAdapter = object : ColumnAdapter<Session.SessionState, String> {
            override fun decode(databaseValue: String): Session.SessionState =
                Json.decodeFromString(databaseValue)
            override fun encode(value: Session.SessionState): String = Json.encodeToString(value)
        },
        question_idsAdapter = object : ColumnAdapter<List<String>, String> {
            override fun decode(databaseValue: String): List<String> =
                Json.decodeFromString(databaseValue)
            override fun encode(value: List<String>): String = Json.encodeToString(value)
        }
    )
}