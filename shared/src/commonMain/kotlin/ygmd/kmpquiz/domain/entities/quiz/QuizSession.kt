package ygmd.kmpquiz.domain.entities.quiz

import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.qanda.Qanda

data class QuizSession(
    val qandas: List<Qanda>,
    val currentIndex: Int = 0,
    // <index, reponse>
    val userAnswers: Map<Int, AnswerContent> = emptyMap(),
){
    val currentQanda: Qanda?
        get() = qandas.getOrNull(currentIndex)
    val isComplete: Boolean
        get() = currentIndex >= qandas.size
}
