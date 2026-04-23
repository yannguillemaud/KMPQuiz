package ygmd.kmpquiz.domain.repository

interface RelationRepository {
    suspend fun insertCategoryForQuiz(quizId: String, categoryId: String): Result<Unit>
    suspend fun removeCategoryForQuiz(quizId: String, categoryId: String): Result<Unit>

}