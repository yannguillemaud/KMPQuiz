package ygmd.kmpquiz.domain.entities.quiz

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.qanda.Qanda

@Serializable
data class QuizSession(
    val quizId: String,
    val title: String,
    val qandas: List<Qanda>,
    val currentIndex: Int = 0,
    // <index, reponse>
    val userAnswers: Map<Int, Choice> = emptyMap(),
){
    val size: Int
        get() = qandas.size
    val currentQanda: Qanda?
        get() = qandas.getOrNull(currentIndex)
    val isComplete: Boolean
        get() = currentIndex >= qandas.size
}
