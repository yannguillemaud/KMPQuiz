package ygmd.kmpquiz.domain.repository

import ygmd.kmpquiz.domain.model.qanda.Qanda

interface RelationRepository {
    suspend fun getQandasByQuizId(quizId: String): List<Qanda>
    suspend fun insertQandaToQuizId(quizId: String, qanda: Qanda): Result<Unit>
    suspend fun deleteQandaFromQuizId(quizId: String, qanda: Qanda): Result<Unit>
}