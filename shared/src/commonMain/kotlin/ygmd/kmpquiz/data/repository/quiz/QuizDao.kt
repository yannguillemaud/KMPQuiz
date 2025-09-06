package ygmd.kmpquiz.data.repository.quiz

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import ygmd.kmpquiz.domain.entities.quiz.Quiz

// TODO - mv to correct pkg
interface QuizDao {
    fun observeAllQuizzes(): Flow<List<Quiz>>
    fun getAllQuizzes(): List<Quiz>
    fun getQuizById(id: String): Quiz?
    fun insertDraft(draftQuiz: DraftQuiz): String?
    fun updateQuiz(quizId: String, quiz: Quiz)
    fun deleteById(id: String)
}