package ygmd.kmpquiz.domain.model.qanda

data class QandaEdit(
    val id: String?,
    val question: String,
    val categoryId: String?,
    val correctAnswer: String,
    val incorrectAnswers: Map<Int, String>,
){
    val canAddNewIncorrectAnwer: Boolean
        get() = incorrectAnswers.size < 3
}

