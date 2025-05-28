package ygmd.kmpquiz.android.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.model.DownloadedUiModel
import ygmd.kmpquiz.android.model.DownloadedUiModel.DOWNLOADED.toDownloadedStateModel
import ygmd.kmpquiz.android.model.toUiModel
import ygmd.kmpquiz.android.ui.composable.FetchedQandaCard
import ygmd.kmpquiz.viewModel.fetch.DownloadedState
import ygmd.kmpquiz.viewModel.fetch.FetchQandasVModel
import ygmd.kmpquiz.viewModel.fetch.SaveQandasVModel

@Composable
fun DisplayFetchQanda(
    fetchQandasVModel: FetchQandasVModel = koinViewModel(),
    saveQandasVModel: SaveQandasVModel = koinViewModel()
) {
    val fetchState by fetchQandasVModel.fetchedUiState.collectAsState()
    val combinedState by fetchQandasVModel.combinedUiState.collectAsState()

    when {
        combinedState.isLoading -> CenteredCircularProgressIndicator()

        combinedState.error != null -> {
            Text(combinedState.error!!.errorMessage)
        }

        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            LazyColumn {
                items(combinedState.fetchQandas.keys.toList()) { item ->
                    val downloadStatus: DownloadedState = combinedState.fetchQandas[item] ?: DownloadedState.DOWNLOADED

                    FetchedQandaCard(
                        qanda = item.toUiModel(),
                        status = downloadStatus.toDownloadedStateModel(),
                        onSaveClick = {
                            saveQandasVModel.saveQanda(item)
                        }
                    )
                }
            }
        }
    }
}