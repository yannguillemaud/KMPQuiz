package ygmd.kmpquiz.data.repository.quiz

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.QuizEntity
import ygmd.kmpquiz.domain.dao.QuizDao


private val logger = Logger.withTag("PersistenceQuizDao")

class PersistenceQuizDao(
    database: KMPQuizDatabase,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : QuizDao {
    private val quizQueries = database.quizQueries
    private val relationQueries = database.relationQueries

    override fun observeAllQuizzes(): Flow<List<QuizEntity>> {
        return quizQueries.selectAll()
            .asFlow()
            .mapToList(dispatchers)
    }

    override fun getAllQuizzes(): List<QuizEntity> {
        return quizQueries.selectAll()
            .executeAsList()
    }

    override fun getQuizById(id: String): QuizEntity? {
        return quizQueries.getById(id).executeAsOneOrNull()
    }

    override fun deleteById(id: String) {
        relationQueries.deleteQandasToQuiz(id)
        val result = quizQueries.deleteById(id)
        if (result.value == 0L) throw IllegalStateException("Quiz $id not found")
    }

    override fun save(entity: QuizEntity): String? = try {
        quizQueries.insert(entity)
        entity.id
    } catch (e: Exception) {
        logger.e(e) { "Error saving quiz $entity" }
        null
    }
}