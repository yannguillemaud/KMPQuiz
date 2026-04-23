package ygmd.kmpquiz.domain.viewModel.displayable

import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.Question

data class DisplayableQanda(
    val id: String,
    val contextKey: String,
    val category: DisplayableCategory,
    val question: Question,
    val answers: Answers,
)