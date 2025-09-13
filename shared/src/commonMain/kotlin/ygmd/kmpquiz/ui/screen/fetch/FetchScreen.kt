package ygmd.kmpquiz.ui.screen.fetch

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.coordinator.FetchAndSavedState
import ygmd.kmpquiz.domain.viewModel.coordinator.FetchScreenCoordinator
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.save.SavedQandasViewModel

@Composable
fun FetchScreen(
    fetchScreenCoordinator: FetchScreenCoordinator = koinViewModel(),
    fetchQandasViewModel: FetchQandasViewModel = koinViewModel(),
    saveQandasViewModel: SavedQandasViewModel = koinViewModel()
){
    val state = fetchScreenCoordinator.fetchAndSavedState.collectAsState(FetchAndSavedState())

    when {
        state.value.isLoading -> {
            CircularProgressIndicator()
        }
        state.value.error != null  -> {
            Text("Error: ${state.value.error}")
        }
        else -> {
            LazyColumn {
                items(items = state.value.qandaByContextKey.values.toList(), key = { it.contextKey }) {
//                    QandaCard(it)
                    Text("ContextKey: ${it.contextKey}")
                }
            }
        }
    }
}