package ygmd.kmpquiz.core.domain.quiz

import ygmd.kmpquiz.core.domain.category.CategoryWithCount
import ygmd.kmpquiz.core.domain.quiz.config.QuizQuestionsConfiguration
import ygmd.kmpquiz.core.domain.quiz.config.QuizSchedulerConfiguration

data class Quiz(
    val id: String,
    val title: String,
    val categories: List<CategoryWithCount> = emptyList(),
    val schedulerConfiguration: QuizSchedulerConfiguration? = null,
    val qandasConfiguration: QuizQuestionsConfiguration,
) {
    val isSchedulerEnabled: Boolean = schedulerConfiguration?.isEnabled == true
    val questionsCount: Int = when (qandasConfiguration) {
        is QuizQuestionsConfiguration.ByCategory -> qandasConfiguration.limitByCategory.values.sum()
        is QuizQuestionsConfiguration.TotalLimited -> qandasConfiguration.count
        else -> categories.sumOf { it.questionsCount }
    }
}