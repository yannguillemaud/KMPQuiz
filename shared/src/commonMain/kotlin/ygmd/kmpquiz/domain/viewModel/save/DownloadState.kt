package ygmd.kmpquiz.domain.viewModel.save

sealed class DownloadState {
    data object NotDownloaded : DownloadState()
    data object Downloading : DownloadState()
    data object Downloaded : DownloadState()
    data class Error(val message: String, val throwable: Throwable? = null) : DownloadState()
}