package ygmd.kmpquiz.data.repository.relation

import ygmd.kmpquiz.domain.dao.RelationDao
import ygmd.kmpquiz.domain.repository.RelationRepository

class RelationRepositoryImpl(
    private val relationDao: RelationDao,
) : RelationRepository {
    override suspend fun insertCategoryForQuiz(
        quizId: String,
        categoryId: String
    ): Result<Unit> {
        return try {
            relationDao.insertCategoryForQuiz(quizId, categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeCategoryForQuiz(
        quizId: String,
        categoryId: String
    ): Result<Unit> {
        return try {
            relationDao.removeCategoryForQuiz(quizId, categoryId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}