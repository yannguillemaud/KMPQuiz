package ygmd.kmpquiz.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import ygmd.kmpquiz.domain.Quiz
import ygmd.kmpquiz.domain.QuizFetchPort
import ygmd.kmpquiz.domain.QuizRepository
import ygmd.kmpquiz.domain.QuizUseCase

class QuizService(
    val quizFetchPort: QuizFetchPort,
    val quizRepository: QuizRepository,
): QuizUseCase {
    override fun getAvailableQuizzes(): List<Quiz> {
        return quizRepository.findAll()
    }

    override suspend fun fetchQuizzes(): List<Quiz> {
        return quizFetchPort.fetchQuizzes()
            .fold(
                onFailure = {
                    print("ERROR WHEN FETCHING")
                    emptyList()
                },
                onSuccess = { it }
            )
    }
}