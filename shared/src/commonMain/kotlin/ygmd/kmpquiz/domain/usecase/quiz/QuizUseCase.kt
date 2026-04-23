package ygmd.kmpquiz.domain.usecase.quiz

import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository
import ygmd.kmpquiz.domain.scheduler.TaskScheduler

/**
 * Use case for quizzes.
 * @param quizRepository The repository to use for quizzes.
 * @param taskScheduler The task scheduler to use for scheduling tasks.
 */
class QuizUseCase(
    private val quizRepository: QuizRepository,
    private val taskScheduler: TaskScheduler,
) {
    suspend fun getById(quizId: String): Quiz? = quizRepository.getQuizById(quizId).getOrNull()

    suspend fun save(quiz: Quiz): Result<Unit> {
        if (quiz.title.isEmpty()) return Result.failure(IllegalStateException("Title cannot be empty"))
        if (quiz.categories.isEmpty()) return Result.failure(IllegalStateException("Categories cannot be empty"))
        return quizRepository.saveQuiz(quiz).mapCatching {
            if(quiz.cron != null && quiz.cron.isEnabled){
                taskScheduler.updateQuizScheduling(quiz.id, quiz.cron)
            }
        }
    }
}