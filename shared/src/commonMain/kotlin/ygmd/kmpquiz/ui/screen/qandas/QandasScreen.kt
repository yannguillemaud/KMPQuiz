package ygmd.kmpquiz.ui.screen.qandas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.save.SaveUiState
import ygmd.kmpquiz.domain.viewModel.save.SavedQandasViewModel

@Composable
fun QandasScreen(
    qandasViewModel: SavedQandasViewModel = koinViewModel()
){
    val saved = qandasViewModel.saveState.collectAsState(SaveUiState())
}