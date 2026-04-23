package ygmd.kmpquiz.domain.dao

interface RelationDao {
    suspend fun insertCategoryForQuiz(quizId: String, categoryId: String)
    suspend fun removeCategoryForQuiz(quizId: String, categoryId: String)
}