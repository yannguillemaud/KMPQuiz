package ygmd.kmpquiz.domain.viewModel.displayable

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.QuestionContent

data class DisplayableQanda(
    val id: String,
    val contextKey: String,
    val category: DisplayableCategory,
    val question: QuestionContent,
    val answers: Answers,
)