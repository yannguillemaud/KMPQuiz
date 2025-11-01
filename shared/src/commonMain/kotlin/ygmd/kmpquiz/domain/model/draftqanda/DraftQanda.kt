package ygmd.kmpquiz.domain.model.draftqanda

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.Question

data class DraftQanda(
    val question: Question,
    val answers: Answers,
    val categoryName: String,
){
    val contextKey: String = "${question.contextKey}|${answers.contextKey}"
}