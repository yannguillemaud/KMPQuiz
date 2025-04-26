package ygmd.kmpquiz.domain.model.quiz

import androidx.compose.runtime.Immutable
import ygmd.kmpquiz.domain.model.cron.QuizCron

@Immutable
data class QuizEdit(
    val id: String?,
    val title: String,
    val categories: List<String>,
    val cron: QuizCron?,
    val error: String? = null,
)
