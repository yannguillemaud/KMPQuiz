package ygmd.kmpquiz.android.ui.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.android.ui.composable.MenuCard
import ygmd.kmpquiz.android.ui.composable.MinimalMenuCard
import ygmd.kmpquiz.android.ui.composable.footer.Footer
import ygmd.kmpquiz.android.ui.composable.header.HomeHeader
import ygmd.kmpquiz.android.ui.composable.header.StatsCard
import ygmd.kmpquiz.viewModel.save.SavedQandasUiState

@Composable
fun HomeScreen(
    onNavigateToFetch: () -> Unit,
    onNavigateToSaved: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToNotifications: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            MenuCard(title = "Fetch") { onNavigateToFetch() }
            MenuCard(title = "Saved") { onNavigateToSaved() }
            MenuCard(title = "Quiz") { onNavigateToQuiz() }
            MenuCard(title = "Notifications") { onNavigateToNotifications }
        }
    }
}