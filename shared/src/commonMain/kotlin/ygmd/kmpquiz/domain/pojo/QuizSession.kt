package ygmd.kmpquiz.domain.pojo

data class QuizSession(
    val qandas: List<InternalQanda>,
    var currentIndex: Int = 0,
    // <index, reponse>
    val userAnswers: Map<Int, String> = emptyMap(),
){
    val currentQanda: InternalQanda?
        get() = qandas.getOrNull(currentIndex)
    val isLastQuestion: Boolean
        get() = currentIndex >= qandas.size-1
    val isComplete: Boolean
        get() = currentIndex == qandas.size
}
