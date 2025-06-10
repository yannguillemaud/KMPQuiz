package ygmd.kmpquiz.android.ui.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.MinimalMenuCard
import ygmd.kmpquiz.android.ui.composable.footer.Footer
import ygmd.kmpquiz.android.ui.composable.header.HomeHeader
import ygmd.kmpquiz.android.ui.composable.header.StatsCard
import ygmd.kmpquiz.viewModel.save.SavedQandasUiState
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel

@Composable
fun HomeScreen(
    onNavigateToFetch: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToParams: () -> Unit,
    savedQandasViewModel: SavedQandasViewModel = koinViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        val savedState = savedQandasViewModel.savedState.collectAsState()

        // Header
        HomeHeader()

        // Stats
        when (val state = savedState.value) {
            is SavedQandasUiState.Loading -> StatsCard(0, 0, 0)
            is SavedQandasUiState.Success -> StatsCard(
                qandasSavedCount = state.qandas.size,
                qandasScheduledCount = 0,
                qandasPlayedCount = 0,
            )
        }

        // Menu principal
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MinimalMenuCard(
                title = "Découvrir",
                subtitle = "Explorez de nouveaux quiz",
                icon = Icons.Filled.Search,
                accentColor = Color(0xFF4F46E5),
                onClick = onNavigateToFetch
            )

            MinimalMenuCard(
                title = "Mes Quiz",
                subtitle = "Vos quiz sauvegardés",
                icon = Icons.Filled.BookmarkBorder,
                accentColor = Color(0xFF10B981),
                onClick = onNavigateToSaved
            )

            MinimalMenuCard(
                title = "Statistiques",
                subtitle = "Suivez vos performances",
                icon = Icons.Filled.Analytics,
                accentColor = Color(0xFFF59E0B),
                onClick = { /* TODO: Navigation vers statistiques */ },
                isComingSoon = true
            )

            MinimalMenuCard(
                title = "Paramètres",
                icon = Icons.Filled.Settings,
                accentColor = Color(0xFF6B7280),
                onClick = { onNavigateToParams() },
                isComingSoon = false
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer
        Footer()
    }
}