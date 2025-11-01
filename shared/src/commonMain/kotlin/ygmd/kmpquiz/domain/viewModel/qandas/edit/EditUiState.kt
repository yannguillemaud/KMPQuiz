package ygmd.kmpquiz.domain.viewModel.qandas.edit

import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory

data class QandaEditUiState(
    val question: TextField,
    val category: DisplayableCategory?,
    val correctAnswer: TextField,
    val incorrectAnswers: Map<Int, TextField> = mapOf(0 to TextField("", "Incorrect answer")),
    val canAddIncorrectAnswer: Boolean = true,
)

data class TextField(
    val value: String,
    val fieldName: String,
) {
    val error: String?
        get() = if (value.isBlank()) {
            "$fieldName cannot be blank"
        } else {
            null
        }
}