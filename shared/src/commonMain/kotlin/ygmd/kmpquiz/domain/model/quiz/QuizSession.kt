package ygmd.kmpquiz.domain.model.quiz

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Qanda

data class QuizSession(
    val quiz: Quiz,
    val currentIndex: Int = 0,
    val userAnswers: Map<Int, Choice> = emptyMap(),
    val currentShuffledAnswers: Answers?,
) {
    val isCompleted: Boolean
        get() = currentIndex >= quiz.qandas.size

    val selectedAnswer: Choice? = userAnswers[currentIndex]

    val currentQanda: Qanda? get() = quiz.qandas.getOrNull(currentIndex)
}

// TODO
data class QuizResult(
    val questions: Int,
    val score: Int,
) {
    val percentage: Int
        get() = if (questions > 0) (score * 100) / questions else 0
}
