package ygmd.kmpquiz.domain.model.quiz

import ygmd.kmpquiz.domain.model.category.CategoryWithCount
import ygmd.kmpquiz.domain.model.scheduler.SchedulerConfiguration

data class Quiz (
    val id: String,
    val title: String,
    val categories: List<CategoryWithCount> = emptyList(),
    val schedulerConfiguration: SchedulerConfiguration? = null,
    val qandasConfiguration: QuizConfigDetails,
){
    val questionsCount: Int = when(qandasConfiguration){
        is QuizConfigDetails.ByCategory -> qandasConfiguration.limitByCategory.values.sum()
        is QuizConfigDetails.TotalLimited -> qandasConfiguration.count
        else -> categories.sumOf { it.questionsCount}
    }

    val isSchedulerActive: Boolean = schedulerConfiguration?.isEnabled == true
}