package ygmd.kmpquiz.domain

interface QuizFetchPort {
    suspend fun fetchQuizzes(): Result<List<Quiz>>
}
