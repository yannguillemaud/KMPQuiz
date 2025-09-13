package ygmd.kmpquiz.data.repository.qanda

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.QandaEntity
import ygmd.kmpquiz.domain.dao.QandaDao
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.repository.DraftQanda
import java.util.UUID

private val logger =
    co.touchlab.kermit.Logger.withTag(PersistenceQandaDao::class.simpleName.toString())

class PersistenceQandaDao(
    database: KMPQuizDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : QandaDao {
    private val qandaQueries = database.qandaQueries
    private val relationQueries = database.quizQandasRelationQueries
    private val mapper = QandaMapper()

    override fun observeQandas(): Flow<List<Qanda>> {
        logger.i { "Observing qandas" }
        return qandaQueries.selectAll()
            .asFlow()
            .mapToList(dispatcher)
            .map { qandas ->
                qandas.map {
                    mapper.map(it)
                }
            }
    }

    override fun getAllQandas(): List<Qanda> {
        return qandaQueries.selectAll()
            .executeAsList()
            .map { mapper.map(it) }
    }

    override fun getQandaByContextKey(contextKey: String): Qanda? {
        return getAllQandas().firstOrNull { it.contextKey == contextKey }
    }

    override fun getQandaById(id: String): Qanda? {
        return qandaQueries.getById(id)
            .executeAsOneOrNull()
            ?.let { mapper.map(it) }
    }

    override fun getQandasByCategory(category: String): List<Qanda> {
        return getAllQandas().filter { it.metadata.category == category }
    }

    override fun saveDraft(draftQanda: DraftQanda): String {
        val id = UUID.randomUUID().toString()
        val incorrectAnswersText = draftQanda.answers.incorrectAnswers
            .map {
                when (it) {
                    is Choice.ImageChoice -> it.imageUrl
                    is Choice.TextChoice -> it.text
                }
            }
        val questionUrl = if(draftQanda.question is Question.ImageQuestion) draftQanda.question.imageUrl else null
        val questionText = when(draftQanda.question){
            is Question.TextQuestion -> draftQanda.question.text
            is Question.ImageQuestion -> draftQanda.question.text
        }

        val toInsert = QandaEntity(
            id = id,
            context_key = draftQanda.contextKey,
            question_type = draftQanda.question.type,
            question_text = questionText,
            question_url = questionUrl,
            incorrect_answers_text = mapper.json.encodeToString(incorrectAnswersText),
            correct_answer_text = draftQanda.answers.correctAnswer.contextKey,
            category = draftQanda.category
        )

        try {
            qandaQueries.insert(toInsert)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save qanda: ${draftQanda.contextKey}" }
            throw e
        }

        return id
    }

    override fun saveAllDraft(draftQandas: List<DraftQanda>) {
        draftQandas.forEach { saveDraft(it) }
    }

    override fun updateQanda(
        qandaId: String,
        qanda: Qanda
    ) {
        TODO("Not yet implemented")
    }

    override fun deleteAllQandas() {
        getAllQandas().forEach {
            relationQueries.deleteQandasToQuiz(it.id)
        }
        qandaQueries.deleteAll()
    }

    override fun deleteQandaById(id: String) {
        qandaQueries.deleteById(id)
        relationQueries.deleteQandasToQuiz(id)
    }
}