package ygmd.kmpquiz.domain.usecase.qanda

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.model.qanda.AnswersFactory
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.qanda.QandaEdit
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.result.SaveQandaResult
import java.util.UUID

private val logger = Logger.withTag("QandaEditUseCase")

class QandaEditUseCase(
    private val qandaRepository: QandaRepository,
    private val qandaEditRepository: QandaEditRepository,
    private val categoryRepository: CategoryRepository,
) {
    fun observeQandaEdit(): Flow<QandaEdit?> = qandaEditRepository.observeQandaEdit()

    suspend fun load(qandaId: String) {
        qandaRepository.getById(qandaId)
            .fold(
                onSuccess = { qandaEditRepository.load(it) },
                onFailure = {
                    logger.e(it) { "Error loading qanda" }
                }
            )
    }

    fun updateQuestion(value: String) {
        qandaEditRepository.updateQuestion(value)
    }

    fun addNewIncorrectAnswer() {
        qandaEditRepository.addNewIncorrectAnswer()
    }

    fun updateAnswers(
        answerIndex: Int,
        value: String,
    ) {
        qandaEditRepository.updateAnswers(answerIndex, value)
    }

    fun updateCorrectAnswer(value: String) {
        qandaEditRepository.updateCorrectAnswer(value)
    }

    fun removeAnswer(answerIndex: Int) {
        if (answerIndex == 0) return
        qandaEditRepository.removeIndexAnswer(answerIndex)
    }

    fun updateCategory(categoryId: String) {
        qandaEditRepository.updateCategory(categoryId)
    }

    suspend fun save(): Result<Unit> {
        val qandaEdit =
            qandaEditRepository.get() ?: return Result.failure(Exception("QandaEdit is null"))
        if (qandaEdit.question.isBlank()) return Result.failure(Exception("Question is blank"))
        if (qandaEdit.correctAnswer.isBlank()) return Result.failure(Exception("Correct answer is blank"))
        if (qandaEdit.incorrectAnswers.any { it.value.isBlank() }) return Result.failure(Exception("Incorrect answer is blank"))

        return if (qandaEdit.id == null) {
            insert(qandaEdit)
        } else {
            update(qandaEdit.id, qandaEdit)
        }
    }

    private suspend fun update(
        id: String,
        qandaEdit: QandaEdit
    ): Result<Unit> {
        val qanda = Qanda(
            id = id,
            question = Question.TextQuestion(qandaEdit.question),
            answers = AnswersFactory.createMultipleTextChoices(
                qandaEdit.correctAnswer,
                qandaEdit.incorrectAnswers.values.toList()
            ),
            categoryId = qandaEdit.categoryId
                ?: return Result.failure(Exception("Category is null"))
        )

        val result = qandaRepository.save(qanda)
        return if (result is SaveQandaResult.Error) Result.failure(result.error)
        else Result.success(Unit)
    }

    private suspend fun insert(qandaEdit: QandaEdit): Result<Unit> {
        val qanda = Qanda(
            id = UUID.randomUUID().toString(),
            question = Question.TextQuestion(qandaEdit.question),
            answers = AnswersFactory.createMultipleTextChoices(
                qandaEdit.correctAnswer,
                qandaEdit.incorrectAnswers.values.toList()
            ),
            categoryId = qandaEdit.categoryId
                ?: return Result.failure(Exception("Category is null"))
        )

        val result = qandaRepository.save(qanda)
        return if (result is SaveQandaResult.Error) Result.failure(result.error)
        else Result.success(Unit)
    }

    fun reset() {
        qandaEditRepository.reset()
    }
}

private val qandaEditLogger = Logger.withTag("QandaEditRepository")

class QandaEditRepository {
    private val _qandaEdit = MutableStateFlow<QandaEdit?>(null)

    fun observeQandaEdit(): Flow<QandaEdit?> = _qandaEdit.asStateFlow()

    fun get(): QandaEdit? = _qandaEdit.value

    fun load(qanda: Qanda) {
        _qandaEdit.value = QandaEdit(
            id = qanda.id,
            categoryId = qanda.categoryId,
            question = when (qanda.question) {
                is Question.TextQuestion -> qanda.question.text
                is Question.ImageQuestion -> {
                    qandaEditLogger.w { "ImageQuestion not supported yet" }
                    qanda.question.imageUrl
                }
            },
            correctAnswer = when (qanda.correctAnswer) {
                is Choice.ImageChoice -> qanda.correctAnswer.imageUrl
                is Choice.TextChoice -> qanda.correctAnswer.text
            },
            incorrectAnswers = when (qanda.answers.incorrectAnswers.first()) {
                is Choice.ImageChoice -> qanda.answers.incorrectAnswers.mapIndexed { index, choice -> index to (choice as Choice.ImageChoice).imageUrl }
                is Choice.TextChoice -> qanda.answers.incorrectAnswers.mapIndexed { index, choice -> index to (choice as Choice.TextChoice).text }
            }.toMap().toMutableMap(),
        )
    }

    fun updateQuestion(question: String) {
        _qandaEdit.update {
            it?.copy(question = question)
        }
    }

    fun updateAnswers(
        answerIndex: Int,
        value: String,
    ) {
        _qandaEdit.update {
            it?.copy(incorrectAnswers = it.incorrectAnswers.toMutableMap().apply {
                this[answerIndex] = value
            })
        }
    }

    fun updateCorrectAnswer(value: String) {
        _qandaEdit.update {
            it?.copy(correctAnswer = value)
        }
    }

    fun addNewIncorrectAnswer() {
        _qandaEdit.update {
            it?.copy(incorrectAnswers = (it.incorrectAnswers + (it.incorrectAnswers.size to "")))
        }
    }

    fun removeIndexAnswer(answerIndex: Int) {
        _qandaEdit.update {
            it?.copy(incorrectAnswers = (it.incorrectAnswers - answerIndex))
        }
    }

    fun updateCategory(category: String) {
        _qandaEdit.update {
            it?.copy(categoryId = category)
        }
    }

    fun reset() {
        _qandaEdit.value = null
    }
}