package ygmd.kmpquiz.domain.viewModel.quiz.edit

import androidx.compose.runtime.Immutable
import ygmd.kmpquiz.domain.model.cron.CronExpression
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory

@Immutable
data class QuizEditUiState(
    val title: String = "",
    val titleError: String? = null,

    val categories: List<DisplayableCategory> = emptyList(),
    val cron: CronExpression? = null,
    val cronEnabled: Boolean = false,
)
