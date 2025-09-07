package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.qanda.Answers
import ygmd.kmpquiz.domain.entities.qanda.Metadata
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.Question
import java.util.UUID

data class DraftQanda(
    val question: Question,
    val answers: Answers,
    val category: String,
){
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"

    fun toQanda(id: String = UUID.randomUUID().toString()): Qanda = Qanda(
        id = id,
        question = question,
        answers = answers,
        metadata = Metadata(
            category = category,
            difficulty = null,
        )
    )
}

interface FetchRepository {
    fun observeFetched(): Flow<List<DraftQanda>>

    suspend fun saveDrafted(qandas: List<DraftQanda>): Result<Unit>
    suspend fun removeFetched(qandas: List<DraftQanda>): Result<Unit>
    suspend fun clearAllFetched(): Result<Unit>
}