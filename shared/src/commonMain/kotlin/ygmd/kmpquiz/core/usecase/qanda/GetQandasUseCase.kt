package ygmd.kmpquiz.core.usecase.qanda

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.core.domain.qanda.Qanda
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.quiz.config.QuizQuestionsConfiguration
import ygmd.kmpquiz.core.repository.QandaRepository
import ygmd.kmpquiz.core.repository.QuizRepository

class GetQandaUseCase(
    private val qandaRepository: QandaRepository,
    private val quizRepository: QuizRepository,
) {
    fun observeQandas(): Flow<List<Qanda>> = qandaRepository.observeAll()

    suspend fun getByCategory(category: String): List<Qanda> =
        qandaRepository.getByCategory(category)

    suspend fun getById(id: String): Qanda? = qandaRepository.getById(id)

    suspend fun getForQuiz(quizId: String): List<Qanda> {
        val quiz = quizRepository.getById(quizId).getOrNull() ?: return emptyList()
        val qandas = when (quiz.qandasConfiguration) {
            is QuizQuestionsConfiguration.TotalLimited -> totalLimited(quiz, quiz.qandasConfiguration)
            is QuizQuestionsConfiguration.AllQuestions -> allQuestions(quiz)
            is QuizQuestionsConfiguration.ByCategory -> byCategory(quiz.qandasConfiguration)
        }
        return qandas
    }

    private suspend fun byCategory(qandasConfiguration: QuizQuestionsConfiguration.ByCategory): List<Qanda> =
        qandasConfiguration.limitByCategory.flatMap { (categoryId, limit) ->
            qandaRepository.getByCategory(categoryId)
                .shuffled()
                .take(limit)
        }

    private suspend fun totalLimited(
        quiz: Quiz,
        qandasConfiguration: QuizQuestionsConfiguration.TotalLimited
    ): List<Qanda> = allQuestions(quiz)
        .shuffled()
        .take(qandasConfiguration.count)

    private suspend fun allQuestions(quiz: Quiz): List<Qanda> = quiz.categories.flatMap {
        qandaRepository.getByCategory(it.id)
    }
}

