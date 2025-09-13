package ygmd.kmpquiz.domain.usecase.quiz

import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

class UpdateQuizUseCase(
    private val quizRepository: QuizRepository
) {
    suspend fun updateQuiz(quiz: Quiz, transform: Quiz.() -> Quiz){
        quizRepository.updateQuiz(quiz.id, transform)
    }
}