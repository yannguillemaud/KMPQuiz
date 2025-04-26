package ygmd.kmpquiz.domain.usecase.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.DraftQuiz
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizEdit
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.QuizRepository

class QuizEditUseCase(
    private val quizEditRepository: QuizEditRepository,
    private val quizRepository: QuizRepository,
    private val qandaRepository: QandaRepository,
) {
    fun observeQuizEdit(): Flow<QuizEdit?> = quizEditRepository.observeQuizEdit()

    suspend fun load(quizId: String) {
        quizRepository.getQuizById(quizId)
            .fold(
                onSuccess = { quizEditRepository.load(it) },
                onFailure = { quizEditRepository.onError() }
            )
    }

    fun updateTitle(value: String) {
        quizEditRepository.updateTitle(value)
    }

    fun updateCategories(values: List<String>) {
        quizEditRepository.updateCategories(values)
    }

    fun updateCron(cron: CronExpression?) {
        quizEditRepository.updateCron(cron)
    }

    fun reset(){
        quizEditRepository.reset()
    }

    suspend fun trySave(): Result<String> {
        val quizEdit = quizEditRepository.get() ?: return Result.failure(IllegalStateException("Cannot save quiz in this state"))
        val quizId = quizEdit.id
        if(quizEdit.categories.isEmpty()) return Result.failure(IllegalStateException("Cannot save quiz with no categories"))
        val qandas = quizEdit.categories.flatMap { qandaRepository.getByCategory(it) }
        if (quizId != null) {
            val quiz = Quiz(
                id = quizId,
                title = quizEdit.title,
                qandas = qandas,
                quizCron = quizEdit.cron
            )
            quizRepository.saveQuiz(quizId, quiz)
            return Result.success(quizId)
        } else {
            val draftQuiz = DraftQuiz(
                title = quizEdit.title,
                qandas = qandas,
                cron = quizEdit.cron,
            )
            return quizRepository.insertQuiz(draftQuiz).map { it.id }
        }
    }
}

interface QuizEditRepository {
    fun observeQuizEdit(): Flow<QuizEdit?>
    fun get(): QuizEdit?
    fun load(quiz: Quiz)
    fun updateTitle(value: String)
    fun updateCategories(values: List<String>)
    fun updateCron(cron: CronExpression?)
    fun onError()
    fun reset()
}

class QuizEditRepositoryImpl : QuizEditRepository {
    private val quizEdit: MutableStateFlow<QuizEdit?> = MutableStateFlow(
        QuizEdit(
            id = null,
            title = "",
            categories = emptyList(),
            cron = null
        )
    )

    override fun observeQuizEdit(): Flow<QuizEdit?> {
        return quizEdit.asStateFlow()
    }

    override fun reset() {
        quizEdit.value = null
    }

    override fun updateTitle(value: String) {
        quizEdit.update {
            it?.copy(
                title = value,
            )
        }
    }

    override fun updateCategories(values: List<String>) = quizEdit.update {
        it?.copy(categories = values)
    }

    override fun updateCron(cron: CronExpression?) {
        quizEdit.update { currentState ->
            currentState?.copy(
                cron = cron?.let { currentState.cron?.copy(cron = cron) ?: QuizCron(it) }
            )
        }
    }

    override fun onError() {

    }

    override fun load(quiz: Quiz) {
        quizEdit.value = QuizEdit(
            id = quiz.id,
            title = quiz.title,
            categories = quiz.qandas.map { it.categoryId }.distinct(),
            cron = quiz.quizCron
        )
    }

    override fun get(): QuizEdit? {
        return quizEdit.value
    }
}