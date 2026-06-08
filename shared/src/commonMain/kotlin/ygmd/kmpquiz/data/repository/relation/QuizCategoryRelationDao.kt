package ygmd.kmpquiz.data.repository.relation

import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.dao.RelationDao

class QuizCategoryRelationDao(
    database: KMPQuizDatabase
): RelationDao {
    override suspend fun insertCategoryForQuiz(
        quizId: String,
        categoryId: String
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun removeCategoryForQuiz(quizId: String, categoryId: String) {
        TODO("Not yet implemented")
    }
}