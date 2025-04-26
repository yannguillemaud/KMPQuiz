package ygmd.kmpquiz.domain

import kotlinx.coroutines.flow.Flow

interface QuizUseCase {
    suspend fun fetchQuizzes(): List<Quiz>
    fun getAvailableQuizzes(): List<Quiz>
}