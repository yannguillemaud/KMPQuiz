package ygmd.kmpquiz.domain.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository

class GetQandaUseCase(
    private val qandaRepository: QandaRepository,
    private val quizRepository: QuizRepository,
) {
    fun observeAll(): Flow<List<Qanda>> = qandaRepository.observeAll()

    suspend fun getByCategory(category: String): List<Qanda> =
        qandaRepository.getByCategory(category)

    suspend fun getById(id: String): Qanda? = qandaRepository.getById(id)

    suspend fun getForQuiz(quizId: String): List<Qanda> {
        val quiz = quizRepository.getById(quizId).getOrNull() ?: return emptyList()
        val qandas = when (quiz.qandasConfiguration) {
            is QuizConfigDetails.TotalLimited -> totalLimited(quiz, quiz.qandasConfiguration)
            is QuizConfigDetails.AllQuestions -> allQuestions(quiz)
            is QuizConfigDetails.ByCategory -> byCategory(quiz.qandasConfiguration)
        }
        return qandas
    }

    private suspend fun byCategory(qandasConfiguration: QuizConfigDetails.ByCategory): List<Qanda> =
        qandasConfiguration.limitByCategory.flatMap { (categoryId, limit) ->
            qandaRepository.getByCategory(categoryId)
                .shuffled()
                .take(limit)
        }

    private suspend fun totalLimited(
        quiz: Quiz,
        qandasConfiguration: QuizConfigDetails.TotalLimited
    ): List<Qanda> = allQuestions(quiz)
        .shuffled()
        .take(qandasConfiguration.count)

    private suspend fun allQuestions(quiz: Quiz): List<Qanda> = quiz.categories.flatMap {
        qandaRepository.getByCategory(it.id)
    }
}

