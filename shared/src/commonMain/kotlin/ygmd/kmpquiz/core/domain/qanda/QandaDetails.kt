package ygmd.kmpquiz.core.domain.qanda

data class QandaDetails(
    val categoryName: String,
    val question: QuestionContent,
    val answers: Answers,
){
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
}