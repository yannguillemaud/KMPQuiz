package ygmd.kmpquiz.domain.model.quiz.session

/**
 * Represents the aggregated statistics for an entire quiz session.
 */
data class SessionStats(
    val totalQuestions: Int,
    val totalCorrect: Int,
    val totalIncorrect: Int,
    val categoriesStats: List<CategoryStat>
) {
    val globalSuccessRate: Float
        get() = if (totalQuestions > 0) totalCorrect.toFloat() / totalQuestions else 0f
}

/**
 * Represents the statistics broken down by a specific category.
 */
data class CategoryStat(
    val categoryId: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val incorrectAnswers: Int
) {
    val successRate: Float
        get() = if (totalQuestions > 0) correctAnswers.toFloat() / totalQuestions else 0f
}

data class SessionResults(
    val userResults: Map<QandaId, UserAnswer>,
    val stats: SessionStats,
){
    data class UserAnswer(
        val answerId: AnswerId,
        val isCorrect: Boolean,
    )
}