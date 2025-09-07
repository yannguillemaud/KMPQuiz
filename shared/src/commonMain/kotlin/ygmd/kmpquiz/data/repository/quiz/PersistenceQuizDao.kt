package ygmd.kmpquiz.data.repository.quiz

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import ygmd.kmpquiz.data.repository.qanda.QandaMapper
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import java.util.UUID


private val logger = Logger.withTag("PersistenceQuizDao")

class PersistenceQuizDao(
    database: KMPQuizDatabase,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : QuizDao {
    private val quizQueries = database.quizQueries
    private val qandaQueries = database.qandaQueries
    private val relationQueries = database.quizQandasRelationQueries
    private val mapper = QandaMapper()

    override fun observeAllQuizzes(): Flow<List<Quiz>> {
        val quizFlow = quizQueries.selectAllQuizzes().asFlow().mapToList(dispatchers)
        val relationFlow = relationQueries.getAllRelations().asFlow().mapToList(dispatchers)
        val qandaFlow = qandaQueries.selectAllQandas().asFlow().mapToList(dispatchers)
        return combine(quizFlow, relationFlow, qandaFlow) { quizzes, relations, qandas ->
            quizzes.map { quiz ->
                val qandaIds = relations
                    .filter { it.quiz_id == quiz.id }
                    .map { it.qanda_id }
                val quizQandas = qandaIds
                    .mapNotNull { id -> qandas.find { it.id == id } }
                    .map { mapper.map(it) }
                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    qandas = quizQandas
                )
            }
        }
    }

    override fun getAllQuizzes(): List<Quiz> {
        return quizQueries.selectAllQuizzes()
            .executeAsList()
            .map { quiz ->
                val qandas = relationQueries.getByQandaId(quiz.id)
                    .executeAsList()
                    .groupBy({ it.quiz_id }, { it.qanda_id })
                    .getOrDefault(quiz.id, emptyList())
                    .mapNotNull { qandaId ->
                        getQanda(qandaId)
                    }
                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    qandas = qandas
                    // TODO - cron
                )
            }
    }

    private fun getQanda(id: String): Qanda? = qandaQueries.getById(id)
        .executeAsOneOrNull()
        ?.let { mapper.map(it) }

    override fun getQuizById(id: String): Quiz? {
        return quizQueries.getQuizById(id).executeAsOneOrNull()
            ?.let { quiz ->
                val qandas = relationQueries.getByQandaId(id)
                    .executeAsList()
                    .groupBy({ it.quiz_id }, { it.qanda_id })[id]
                    ?.map { qandaId ->
                        val qandaEntity = qandaQueries.getById(qandaId).executeAsOne()
                        mapper.map(qandaEntity)
                    } ?: emptyList()


                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    qandas = qandas,
                    quizCron = quiz.cron_expression?.let {
                        QuizCron(
                            cron = CronExpression(
                                expression = it,
                                displayName = quiz.cron_display_name ?: "Unknown cron",
                            ),
                        )
                    }
                )
            }
    }

    override fun insertDraft(draftQuiz: DraftQuiz): String? {
        val id = UUID.randomUUID().toString()

        val quizInsertionResult = quizQueries.insertQuiz(
            id = id,
            title = draftQuiz.title,
            cron_expression = draftQuiz.cron?.cron?.expression,
            cron_display_name = draftQuiz.cron?.cron?.displayName,
        )

        if (quizInsertionResult.value != 1L) {
            logger.e { "Failed to insert quiz" }
            throw IllegalStateException("Failed to insert quiz")
        }

        draftQuiz.qandas.forEach { qanda ->
            val qandaInsertionResult = relationQueries.insertRelation(
                quiz_id = id,
                qanda_id = qanda.id,
            )

            if (qandaInsertionResult.value != 1L) {
                logger.e { "Failed to insert qanda" }
                throw IllegalStateException("Failed to insert qanda")
            }
        }

        return id
    }

    override fun deleteById(id: String) {
        relationQueries.deleteQandasToQuiz(id)
        val result = quizQueries.deleteById(id)
        if(result.value == 0L) throw IllegalStateException("Quiz $id not found")
    }

    override fun updateQuiz(
        quizId: String,
        quiz: Quiz,
    ) {
        quizQueries.updateQuiz(
            id = quizId,
            title = quiz.title,
            cron_expression = quiz.quizCron?.cron?.expression,
            cron_display_name = quiz.quizCron?.cron?.displayName,
        )
    }
}