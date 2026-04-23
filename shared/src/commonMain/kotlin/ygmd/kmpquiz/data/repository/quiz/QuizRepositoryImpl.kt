package ygmd.kmpquiz.data.repository.quiz

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.model.category.Category
import ygmd.kmpquiz.domain.model.cron.QuizCron
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails
import ygmd.kmpquiz.domain.repository.QuizRepository
import java.util.UUID

private val logger = Logger.withTag("QuizRepository")

class QuizRepositoryImpl(
    private val database: KMPQuizDatabase,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : QuizRepository {
    override fun observeAll(): Flow<List<Quiz>> {
        val quizzesFlow = database.quizQueries.selectAll().asFlow().mapToList(dispatchers)
        val categoriesFlow =
            database.categoryQueries.selectAllCategoriesWithCount().asFlow().mapToList(dispatchers)
        val categoriesByQuizLookup =
            database.quizCategoryRelationQueries.selectAll().asFlow().mapToList(dispatchers)
                .map { it.groupBy({ it.quiz_id }, { it.category_id }) }

        return combine(
            quizzesFlow,
            categoriesFlow,
            categoriesByQuizLookup
        ) { quizzes, categories, lookup ->
            quizzes.map { quiz ->
                val categoriesForQuiz = lookup[quiz.id]?.mapNotNull {
                    categories.find { category -> category.id == it }
                } ?: emptyList()

                val cron = if (quiz.cronName != null && quiz.cronExpression != null) {
                    QuizCron(
                        id = quiz.cronId ?: UUID.randomUUID().toString(),
                        name = quiz.cronName,
                        expression = quiz.cronExpression,
                        isEnabled = quiz.cronEnabled ?: false
                    )
                } else null

                val questionsCount = when (quiz.configuration) {
                    is QuizConfigDetails.ByCategory -> quiz.configuration.limitByCategory.values.sum()
                    is QuizConfigDetails.TotalLimited -> quiz.configuration.count
                    else -> categoriesForQuiz.sumOf { it.questionCount }.toInt()
                }

                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    categories = categoriesForQuiz.map { Category(it.id, it.name) },
                    cron = cron,
                    config = quiz.configuration ?: QuizConfigDetails.AllQuestions(),
                    questionsCount = questionsCount
                )
            }
        }
    }

    override suspend fun getAllQuizzes(): List<Quiz> =
        database.quizQueries.selectAll().executeAsList()
            .map { quiz ->
                val categories = database.quizCategoryRelationQueries
                    .getCategoriesForQuiz(quiz.id).executeAsList()
                    .map { Category(it.id, it.name) }
                val config = database.quizConfigurationQueries
                    .getConfigForQuiz(quiz.id)
                    .executeAsOneOrNull()
                    ?.config_details ?: QuizConfigDetails.AllQuestions()
                val questionsCount = when (config) {
                    is QuizConfigDetails.ByCategory -> config.limitByCategory.values.sum()
                    is QuizConfigDetails.TotalLimited -> config.count
                    else -> categories.sumOf {
                        database.qandaQueries
                            .countQandasByCategory(it.id)
                            .executeAsOneOrNull() ?: 0L
                    }.toInt()
                }
                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    categories = categories,
                    config = config,
                    questionsCount = questionsCount
                )
            }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        val quizEntity =
            database.quizQueries.getById(id).executeAsOneOrNull() ?: return Result.failure(
                Exception("Quiz not found")
            )
        val categories = database.quizCategoryRelationQueries
            .getCategoriesForQuiz(id).executeAsList()
            .map { Category(it.id, it.name) }
        val config = database.quizConfigurationQueries
            .getConfigForQuiz(id)
            .executeAsOneOrNull()
            ?.config_details ?: QuizConfigDetails.AllQuestions()
        val questionsCount = when (config) {
            is QuizConfigDetails.ByCategory -> config.limitByCategory.values.sum()
            is QuizConfigDetails.TotalLimited -> config.count
            else -> categories.sumOf {
                database.qandaQueries
                    .countQandasByCategory(it.id)
                    .executeAsOneOrNull() ?: 0L
            }.toInt()
        }
        val cron = quizEntity.cron_id?.let { cronId ->
            val cronEntity = database.quizCronQueries.getById(cronId).executeAsOneOrNull()
            if (cronEntity != null) {
                QuizCron(
                    id = cronEntity.id,
                    name = cronEntity.name,
                    expression = cronEntity.expression
                )
            } else null
        }

        val quiz = Quiz(
            id = quizEntity.id,
            title = quizEntity.title,
            categories = categories,
            config = config,
            questionsCount = questionsCount,
            cron = cron
        )
        return Result.success(quiz)
    }

    override suspend fun saveQuiz(
        quiz: Quiz,
    ): Result<Unit> {
        return try {
            val cronId = if (quiz.cron == null) null
            else {
                val existing = database.quizCronQueries.getById(quiz.cron.id).executeAsOneOrNull()
                if (existing != null) existing.id
                else {
                    database.quizCronQueries.insertCron(
                        id = quiz.cron.id,
                        name = quiz.cron.name,
                        expression = quiz.cron.expression
                    )
                    quiz.cron.id
                }
            }

            val result =
                database.quizQueries.insert(
                    id = quiz.id,
                    title = quiz.title,
                    cron_id = cronId,
                    active_scheduling = quiz.cron?.isEnabled
                )
            if (result.value == 0L) {
                logger.e { "Failed to save quiz $quiz" }
                return Result.failure(Exception("Failed to save quiz"))
            }
            updateCategoriesForQuiz(quizId = quiz.id, quiz = quiz)
            updateSettingsConfigForQuiz(quizId = quiz.id, quiz = quiz)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun updateSettingsConfigForQuiz(quizId: String, quiz: Quiz) {
        val result = database.quizConfigurationQueries.insertConfig(
            id = quiz.config.id,
            quiz_id = quizId,
            config_details = quiz.config
        )
        logger.d { "updateSettingsConfigForQuiz : ${result.value}" }
    }

    private fun updateCategoriesForQuiz(quizId: String, quiz: Quiz) {
        val quizCategoryRelationQueries = database.quizCategoryRelationQueries
        val existingCategoriesForQuiz = quizCategoryRelationQueries
            .getCategoriesForQuiz(quizId)
            .executeAsList()
            .map { it.id }
        val quizCategoriesId = quiz.categories.map { it.id }
        val categoriesToDelete = existingCategoriesForQuiz.filter { !quizCategoriesId.contains(it) }
        categoriesToDelete.forEach { categoryId ->
            if (quizCategoryRelationQueries.removeCategoryForQuiz(
                    quiz_id = quizId,
                    category_id = categoryId
                ).value == 0L
            ) logger.e { "Failed to delete category $categoryId for quiz $quizId" }
        }
        val categoriesToAdd = quizCategoriesId.filter { !existingCategoriesForQuiz.contains(it) }
        categoriesToAdd.forEach { categoryId ->
            quizCategoryRelationQueries.insertQuizCategoryRelation(
                quiz_id = quizId,
                category_id = categoryId
            )
        }
    }

    override suspend fun deleteQuizById(id: String): Result<Unit> {
        val result = database.quizQueries.deleteById(id)
        if (result.value == 0L) return Result.failure(Exception("Failed to delete quiz"))
        return Result.success(Unit)
    }

    override suspend fun toggleCron(
        quizId: String,
        newValue: Boolean
    ): Result<Unit> {
        val result = database.quizQueries.scheduleQuiz(newValue, quizId)
        if (result.value == 0L) return Result.failure(Exception("Failed to toggle cron"))
        return Result.success(Unit)
    }
}