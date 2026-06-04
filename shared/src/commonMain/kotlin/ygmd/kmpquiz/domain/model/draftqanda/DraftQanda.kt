package ygmd.kmpquiz.domain.model.draftqanda

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.QuestionContent

data class DraftQanda(
    val question: QuestionContent,
    val answers: Answers,
    val categoryName: String,
){
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
}