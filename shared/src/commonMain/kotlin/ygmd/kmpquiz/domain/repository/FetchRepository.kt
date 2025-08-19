package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.Companion.createMultipleTextChoice
import ygmd.kmpquiz.domain.entities.qanda.AnswerSet.Companion.createTrueFalse
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QandaMetadata
import ygmd.kmpquiz.domain.entities.qanda.QuestionContent

data class DraftQanda(
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    val category: String,
    val isSaved: Boolean = false,
){
    val contextKey: String = "$question|$correctAnswer"

    fun toQanda(id: Long): Qanda {
        val isBooleanQuestion = answers.size == 2

        return Qanda(
            id = id,
            question = QuestionContent.TextContent(question),
            answers = if (isBooleanQuestion) createTrueFalse(correctAnswer.toBoolean())
                else createMultipleTextChoice(correctAnswer, answers),
            metadata = QandaMetadata(
                category = category,
                difficulty = null,
            )
        )
    }
}

interface FetchRepository {
    fun observeFetched(): Flow<List<DraftQanda>>

    suspend fun saveDrafted(qandas: List<DraftQanda>): Result<Unit>
    suspend fun removeFetched(qandas: List<DraftQanda>): Result<Unit>
    suspend fun clearAllFetched(): Result<Unit>
}