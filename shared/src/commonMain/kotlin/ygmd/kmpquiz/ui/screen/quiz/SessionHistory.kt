package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.model.quiz.session.QuizSession
import ygmd.kmpquiz.domain.viewModel.quiz.session.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionHistoryScreen(
    viewModel: SessionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToSessionDetails: (sessionId: String) -> Unit = {},
    onContinueSession: (sessionId: String) -> Unit = { },
) {
    val sessionHistory by viewModel.sessions.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Session History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Transparent)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (sessionHistory.isLoading) CircularProgressIndicator()
            LazyColumn(
                contentPadding = PaddingValues(12.dp)
            ) {
                items(sessionHistory.sessions, key = { it.sessionId }) {
                    ListItem(
                        modifier = Modifier.clickable(enabled = it.historyAvailable) {
                            onNavigateToSessionDetails(it.sessionId)
                        },
                        headlineContent = {
                            Text(it.quizTitle)
                        },
                        supportingContent = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val stateText = when (it.sessionState) {
                                    is QuizSession.SessionState.Completed -> "Completed"
                                    is QuizSession.SessionState.NotStarted -> "Not Started"
                                    is QuizSession.SessionState.InProgress -> "In Progress"
                                }
                                Text(stateText)
                                if (it.sessionState is QuizSession.SessionState.Completed)
                                    Text(it.updatedAt)
                            }
                        },
                        trailingContent = {
                            if (it.sessionState !is QuizSession.SessionState.Completed) {
                                IconButton(onClick = { onContinueSession(it.sessionId) }) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.Reply,
                                        contentDescription = "Play"
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