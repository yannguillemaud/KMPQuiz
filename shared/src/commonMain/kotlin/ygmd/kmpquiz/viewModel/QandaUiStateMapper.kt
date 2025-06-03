package ygmd.kmpquiz.viewModel

interface QandaUiStateMapper {

}

//
//class QandaUiStateMapperImpl : QandaUiStateMapper {
//    override fun mapToUiState(
//        fetched: List<InternalQanda>,
//        saved: List<InternalQanda>,
//        downloadStates: Map<String, DownloadState>
//    ): List<QandaUiState> =
//        fetched.map { qanda ->
//            QandaUiState(
//                qanda = qanda,
//                isSaved = saved.any { it.contentKey() == qanda.contentKey() },
//                downloadState = downloadStates[qanda.contentKey()]
//                    ?: DownloadState.NotDownloaded,
//            )
//        }
//}