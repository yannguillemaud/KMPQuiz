package ygmd.kmpquiz.domain.pojo.quiz

import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda

data class QuizSession(
    val qandas: List<InternalQanda>,
    val currentIndex: Int = 0,
    // <index, reponse>
    val userAnswers: Map<Int, String> = emptyMap(),
){
    val currentQanda: InternalQanda?
        get() = qandas.getOrNull(currentIndex)
    val isComplete: Boolean
        get() = currentIndex >= qandas.size
}
