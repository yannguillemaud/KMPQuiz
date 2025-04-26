package ygmd.kmpquiz.domain.usecase.quiz

import co.touchlab.kermit.Logger
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.quiz.DraftQuiz
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("CreateQuizUseCase")

class CreateQuizUseCase(
    private val quizRepository: QuizRepository,
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
            val quizDraft = DraftQuiz(
                title = title,
                qandas = qandas,
                cron = cron
            )
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

}