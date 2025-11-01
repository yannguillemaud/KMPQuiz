package ygmd.kmpquiz.domain.dao

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.QuizEntity

// TODO - mv to correct pkg
interface QuizDao {
    fun observeAllQuizzes(): Flow<List<QuizEntity>>
    fun getAllQuizzes(): List<QuizEntity>
    fun getQuizById(id: String): QuizEntity?
    fun save(entity: QuizEntity): String?
    fun deleteById(id: String)
}