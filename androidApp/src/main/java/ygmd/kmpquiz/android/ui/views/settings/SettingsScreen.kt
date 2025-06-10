package ygmd.kmpquiz.android.ui.views.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel
import ygmd.kmpquiz.viewModel.settings.SettingsViewModel

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    savedQandasViewModel: SavedQandasViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
){
    val savedState = savedQandasViewModel.savedState.collectAsState()
    val settingsState = settingsViewModel.userSettings.collectAsState()

    // SOON TM
}