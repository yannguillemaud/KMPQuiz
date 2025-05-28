package ygmd.kmpquiz.android.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import ygmd.kmpquiz.android.model.DownloadedUiModel.NOT_DOWNLOADED
import ygmd.kmpquiz.android.ui.composable.DifficultyProperties
import ygmd.kmpquiz.android.ui.composable.DifficultyProperties.UNKNOWN
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.viewModel.fetch.DownloadedState

data class FetchedQandaModel(
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswerPosition: Int,
    val difficultyProperties: DifficultyProperties = UNKNOWN,
    val downloadedState: DownloadedUiModel = NOT_DOWNLOADED,
)

sealed class DownloadedUiModel(
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
) {
    data object DOWNLOADED :
        DownloadedUiModel("Téléchargé", Icons.Default.CheckCircle, Color(0xFF4CAF50))

    data object NOT_DOWNLOADED :
        DownloadedUiModel("Non téléchargé", Icons.Default.ShoppingCart, Color(0xFF757575))

    data object DOWNLOADING :
        DownloadedUiModel("Téléchargement...", Icons.Default.ThumbUp, Color(0xFF2196F3))

    data object ERROR : DownloadedUiModel("Erreur", Icons.Default.Warning, Color(0xFFF44336))

    fun DownloadedState.toDownloadedStateModel(): DownloadedUiModel = when(this) {
        is DownloadedState.DOWNLOADED -> DOWNLOADED
        is DownloadedState.NOT_DOWNLOADED -> NOT_DOWNLOADED
        is DownloadedState.DOWNLOADING -> DOWNLOADING
        is DownloadedState.ERROR -> ERROR
    }
}

fun InternalQanda.toUiModel() = FetchedQandaModel(
    category = category,
    question = question,
    answers = answers,
    correctAnswerPosition = correctAnswerPosition,
    difficultyProperties = difficulty.asDifficultyPropertiesOrUnknown(),
)

fun FetchedQandaModel.toInternalQanda() = InternalQanda(
    category = category,
    question = question,
    answers = answers,
    correctAnswerPosition = correctAnswerPosition,
    difficulty = difficultyProperties.displayName
)

private fun String?.asDifficultyPropertiesOrUnknown() =
    when (this?.lowercase()) {
        "easy" -> DifficultyProperties.EASY
        "medium" -> DifficultyProperties.MEDIUM
        "hard" -> DifficultyProperties.HARD
        else -> UNKNOWN
    }