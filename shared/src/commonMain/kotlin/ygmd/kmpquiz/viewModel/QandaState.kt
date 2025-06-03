package ygmd.kmpquiz.viewModel

import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.viewModel.save.DownloadState

enum class QandaStatus {
    AVAILABLE, // Pas encore save, prêt à être sauvegardé
    SAVED, // Sauvegardé
    DOWNLOADING, // En cours de save
    ERROR, // Erreur lors de la sauvegarde
}

enum class Difficulty {
    HARD,
    MEDIUM,
    EASY,
    UNKNOWN,
}

data class QandaUiState(
    val qanda: InternalQanda,
    val downloadState: DownloadState,
){
    val status: QandaStatus
        get() = when (downloadState) {
            is DownloadState.Downloading -> QandaStatus.DOWNLOADING
            is DownloadState.Error -> QandaStatus.ERROR
            else -> QandaStatus.AVAILABLE
        }

    val isSaved = status == QandaStatus.SAVED
    val isSaving = status == QandaStatus.DOWNLOADING

    val difficulty: Difficulty
        get() = when(qanda.difficulty.lowercase()){
            "easy" -> Difficulty.EASY
            "medium" -> Difficulty.MEDIUM
            "hard" -> Difficulty.HARD
            else -> Difficulty.UNKNOWN
        }
}