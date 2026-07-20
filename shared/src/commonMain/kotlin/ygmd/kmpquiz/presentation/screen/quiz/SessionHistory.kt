package ygmd.kmpquiz.presentation.screen.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    viewModel: SessionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToSessionDetails: (sessionId: String) -> Unit = {},
    onContinueSession: (sessionId: String) -> Unit = { },
) {
    val sessionHistory by viewModel.sessions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("History", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Transparent)
                )
                /** Incoming Feature
                PrimaryTabRow(selectedTabIndex = 1) {
                    Tab(
                        selected = false,
                        onClick = onNavigateBack,
                        text = { Text("Quizzes") }
                    )
                    Tab(
                        selected = true,
                        onClick = {},
                        text = { Text("History") }
                    )
                }
                **/
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                sessionHistory.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                sessionHistory.sessions.isEmpty() -> {
                    EmptyState(
                        modifier = Modifier.fillMaxSize(),
                        icon = Icons.Outlined.History,
                        title = "No sessions yet",
                        description = "Play a quiz to see your history here.",
                        fullSizeState = true
                    )
                }

                else -> {
                    LazyColumn(
                        // Bottom padding reserves space for the floating nav capsule so the
                        // last session row is not obscured on long histories.
                        contentPadding = PaddingValues(
                            start = Dimens.PaddingSmall,
                            top = Dimens.PaddingSmall,
                            end = Dimens.PaddingSmall,
                            bottom = Dimens.BottomNavPadding,
                        ),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sessionHistory.sessions, key = { it.sessionId }) { session ->
                            ListItem(
                                modifier = Modifier
                                    .clickable(enabled = session.historyAvailable) {
                                        onNavigateToSessionDetails(session.sessionId)
                                    }
                                    .alpha(if (session.historyAvailable) 1f else 0.5f),
                                headlineContent = {
                                    Text(session.quizTitle)
                                },
                                supportingContent = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val stateText = when (session.sessionState) {
                                            is Session.SessionState.Completed -> "Completed"
                                            is Session.SessionState.InProgress -> "In Progress"
                                        }
                                        Text(stateText)
                                        if (session.sessionState is Session.SessionState.Completed)
                                            Text(session.updatedAt)
                                    }
                                },
                                trailingContent = {
                                    if (session.sessionState !is Session.SessionState.Completed) {
                                        IconButton(onClick = { onContinueSession(session.sessionId) }) {
                                            Icon(
                                                Icons.AutoMirrored.Outlined.Reply,
                                                contentDescription = "Resume session"
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
