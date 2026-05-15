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
import ygmd.kmpquiz.domain.model.category.CategoryWithCount
import ygmd.kmpquiz.domain.model.cron.SchedulerConfiguration
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.repository.QuizRepository

private val logger = Logger.withTag("QuizRepository")

class QuizRepositoryImpl(
    private val database: KMPQuizDatabase,
    private val dispatchers: CoroutineDispatcher = Dispatchers.IO,
) : QuizRepository {
    override fun observeAll(): Flow<List<Quiz>> {
        val allQuizzes = database.quizQueries.selectAll()
            .asFlow().mapToList(dispatchers)
        val categoriesByQuiz =
            database.quizCategoryRelationQueries.getAllCategoriesConfigurationWithCount()
                .asFlow().mapToList(dispatchers)
                .map { relation -> relation.groupBy { it.quizId } }
        val schedulerConfigurations = database.schedulerConfigurationQueries.getAll()
            .asFlow().mapToList(dispatchers)
            .map { schedulers -> schedulers.associateBy({ it.quiz_id }, { it }) }
        return combine(
            allQuizzes,
            categoriesByQuiz,
            schedulerConfigurations,
        ) { quizzes, categories, schedulerConfigurations ->
            quizzes.map { quiz ->
                val categories = categories[quiz.id].orEmpty()
                    .map { category ->
                        CategoryWithCount(
                            category.categoryId,
                            category.categoryName,
                            category.categoryCount.toInt()
                        )
                    }
                val questionsConfiguration = quiz.questions_config
                val schedulerConfiguration = schedulerConfigurations[quiz.id]?.let {
                    SchedulerConfiguration(
                        id = it.id,
                        selection = it.selection,
                        isEnabled = it.enabled == true
                    )
                }
                Quiz(
                    id = quiz.id,
                    title = quiz.title,
                    categories = categories,
                    qandasConfiguration = questionsConfiguration,
                    schedulerConfiguration = schedulerConfiguration,
                )
            }
        }
    }

    override suspend fun getAllQuizzes(): List<Quiz> {
        val categoriesByQuizId = database.quizCategoryRelationQueries
            .getAllCategoriesConfigurationWithCount()
            .executeAsList()
            .groupBy { it.quizId }
        val schedulersConfigurationsByQuizId = database.schedulerConfigurationQueries
            .getAll().executeAsList()
            .associate {
                it.id to SchedulerConfiguration(
                    id = it.id,
                    selection = it.selection,
                    isEnabled = it.enabled == true
                )
            }
        return database.quizQueries.selectAll().executeAsList()
            .map { row ->
                val categories = categoriesByQuizId[row.id].orEmpty()
                    .map { category ->
                        CategoryWithCount(
                            category.categoryId,
                            category.categoryName,
                            category.categoryCount.toInt()
                        )
                    }
                val questionsConfiguration = row.questions_config
                val schedulerConfiguration = schedulersConfigurationsByQuizId[row.id]
                Quiz(
                    id = row.id,
                    title = row.title,
                    categories = categories,
                    qandasConfiguration = questionsConfiguration,
                    schedulerConfiguration = schedulerConfiguration
                )
            }
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        val quizEntity = database.quizQueries
            .getById(id)
            .executeAsOneOrNull() ?: return Result.failure(Exception("Quiz not found"))
        val categories = getCategoriesWithCountForQuiz(id)
        val config = quizEntity.questions_config
        val schedulerConfiguration = database.schedulerConfigurationQueries
            .getSchedulerConfigurationForQuiz(quizEntity.id)
            .executeAsOneOrNull()
            ?.let { scheduler -> SchedulerConfiguration(scheduler.id, scheduler.selection, true) }
        val quiz = Quiz(
            id = quizEntity.id,
            title = quizEntity.title,
            categories = categories,
            qandasConfiguration = config,
            schedulerConfiguration = schedulerConfiguration
        )
        return Result.success(quiz)
    }

    private fun getCategoriesWithCountForQuiz(quizId: String): List<CategoryWithCount> =
        database.quizCategoryRelationQueries
            .getCategoriesWithCountForQuiz(quizId).executeAsList()
            .map { (categoryId, categoryName, count) ->
                CategoryWithCount(
                    categoryId,
                    categoryName,
                    count.toInt()
                )
            }

    override suspend fun saveQuiz(
        quiz: Quiz,
    ): Result<Unit> {
        try {
            database.transaction {
                insertQuiz(quiz)
                updateSchedulerConfiguration(quiz.id, quiz.schedulerConfiguration)
                updateCategoriesForQuiz(quiz.id, quiz)
                Result.success(Unit)
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save quiz" }
            return Result.failure(SaveQuizFailedException(quiz.id))
        }
    }

    private fun updateSchedulerConfiguration(
        quizId: String,
        schedulerConfiguration: SchedulerConfiguration?
    ) {
        if (schedulerConfiguration != null) {
            database.schedulerConfigurationQueries.saveSchedulerConfiguration(
                id = schedulerConfiguration.id,
                quiz_id = quizId,
                expression = schedulerConfiguration.cron,
                selection = schedulerConfiguration.selection,
                name = schedulerConfiguration.selection.toString(),
                enabled = schedulerConfiguration.isEnabled
            )
        } else {
            database.schedulerConfigurationQueries.removeSchedulerConfiguration(quizId)
        }
    }

    private fun insertQuiz(quiz: Quiz) {
        database.quizQueries.save(
            id = quiz.id,
            title = quiz.title,
            questions_config = quiz.qandasConfiguration
        )
    }

    private fun updateCategoriesForQuiz(quizId: String, quiz: Quiz) {
        val quizCategoryRelationQueries = database.quizCategoryRelationQueries
        val existingCategoriesForQuiz = quizCategoryRelationQueries
            .getCategoriesWithCountForQuiz(quizId)
            .executeAsList()
            .map { it.id }
        val quizCategoriesId = quiz.categories.map { it.id }
        val categoriesToDelete =
            existingCategoriesForQuiz.filter { !quizCategoriesId.contains(it) }
        categoriesToDelete.forEach { categoryId ->
            if (quizCategoryRelationQueries.removeCategoryForQuiz(
                    quiz_id = quizId,
                    category_id = categoryId
                ).value == 0L
            ) logger.e { "Failed to delete category $categoryId for quiz $quizId" }
        }
        val categoriesToAdd =
            quizCategoriesId.filter { !existingCategoriesForQuiz.contains(it) }
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

    override suspend fun toggleQuizScheduler(
        quizId: String,
        newValue: Boolean
    ): Result<Unit> {
        val result = database.schedulerConfigurationQueries.scheduleQuiz(newValue, quizId)
        if (result.value == 0L) return Result.failure(Exception("Failed to toggle cron"))
        return Result.success(Unit)
    }

    override suspend fun getAllScheduled(): Map<String, SchedulerConfiguration> {
        return database.schedulerConfigurationQueries.getAllScheduled()
            .executeAsList()
            .associate {
                it.id to SchedulerConfiguration(
                    id = it.id,
                    selection = it.selection,
                    isEnabled = it.enabled == true
                )
            }
    }
}

data class SaveQuizFailedException(val quizId: String) : Exception(
    "Quiz with id $quizId not found"
)