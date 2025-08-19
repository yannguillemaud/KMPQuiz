package ygmd.kmpquiz.application.usecase.quiz

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.first
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import ygmd.kmpquiz.domain.entities.quiz.QuizDraft
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("CreateQuizUseCase")

class CreateQuizUseCase(
    private val quizRepository: QuizRepository,
    private val getQandaUseCase: GetQandaUseCase,
    private val getQuizUseCase: GetQuizUseCase,
) {

    suspend fun createQuiz(
        title: String,
        qandas: List<Qanda>,
        cron: QuizCron?,
    ): Result<Quiz> {
        return try {
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Quiz title should not be empty"))
            }
            val quizDraft = QuizDraft(title, qandas, cron)
            val result = quizRepository.insertQuiz(quizDraft)
            if (result.isSuccess) {
                logger.i { "Created quiz $title with ${qandas.size} qandas" }
            }
            result
        } catch (e: Exception) {
            logger.e(e) { "Error when creating quiz $title" }
            Result.failure(e)
        }
    }

    suspend fun createQuizByPredicate(
        title: String,
        predicate: (Qanda) -> Boolean,
        cron: QuizCron?,
    ): Result<Quiz> {
        return try {
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("Quiz title should not be empty"))
            }
            val allQandas = getQandaUseCase.observeSaved().first()
            val filteredQandas = allQandas.filter { predicate(it) }
            if (filteredQandas.isEmpty()) {
                logger.w { "Filtered qandas is empty for quiz $title" }
            }
            val quizDraft = QuizDraft(title, filteredQandas, cron)
            val result = quizRepository.insertQuiz(quizDraft)
            if (result.isSuccess) {
                logger.i { "Created quiz $title with ${filteredQandas.size} qandas" }
            }
            result
        } catch (e: Exception) {
            logger.e(e) { "Error when creating quiz $title" }
            Result.failure(e)
        }
    }
}