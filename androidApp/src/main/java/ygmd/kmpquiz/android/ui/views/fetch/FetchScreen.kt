package ygmd.kmpquiz.android.ui.views.fetch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.views.fetch.error.FetchErrorSection
import ygmd.kmpquiz.android.ui.views.fetch.success.main.EmptySection
import ygmd.kmpquiz.android.ui.views.fetch.success.main.FetchLoadingSection
import ygmd.kmpquiz.android.ui.views.fetch.success.main.SuccessSection
import ygmd.kmpquiz.android.ui.views.fetch.success.topbar.FetchTopBar
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchState
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel

@Composable
fun FetchScreen(
    onNavigateBack: () -> Unit = {},
    fetchViewModel: FetchQandasViewModel = koinViewModel(),
    saveViewModel: SavedQandasViewModel = koinViewModel(),
) {
    val fetchState by fetchViewModel.fetchState.collectAsState(FetchState.Idle)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
/*
        // Header
        FetchTopBar(onNavigateBack = onNavigateBack)

        // Content
        when (val state = fetchState) {
            is FetchState.Loading, FetchState.Idle -> {
                FetchLoadingSection()
            }

            is FetchState.Success -> {
                if (state.availableQandas.isEmpty())
                    EmptySection(onRetry = { fetchViewModel.fetchQandas() })
                else {
                    SuccessSection(
                            state = state,
                            fetchQandasViewModel = fetchViewModel,
                            saveViewModel = saveViewModel,
                        )
                    }
                }
            }

            is FetchState.Error -> {
                FetchErrorSection(
                    error = state.error,
                    onRetry = { fetchViewModel.fetchQandas() }
                )
            }
        }
*/
    }
}