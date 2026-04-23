package ygmd.kmpquiz.domain.viewModel.displayable

data class DisplayableQuizCron(
    val id: String,
    val name: String,
    val expression: String,
    val isEnabled: Boolean,
)