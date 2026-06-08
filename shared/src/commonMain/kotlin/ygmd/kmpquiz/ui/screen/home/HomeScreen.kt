package ygmd.kmpquiz.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.fetch.HomeViewModel
import ygmd.kmpquiz.events.event.Event.SnackbarEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val homeState by viewModel.contentFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is SnackbarEvent) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Categories", fontWeight = FontWeight.SemiBold) },
                        supportingContent = {
                            when {
                                homeState.isDownloading -> {
                                    AnimatedVisibility(
                                        visible = true,
                                        enter = expandVertically(),
                                        exit = shrinkVertically()
                                    ) {
                                        LinearProgressIndicator(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                homeState.totalCategoriesCount == 0 -> Text("No categories yet.")
                                else -> Text("${homeState.totalCategoriesCount} categories.")
                            }
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
                                contentDescription = "Available categories",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            if (homeState.totalCategoriesCount == 0 && !homeState.isDownloading) {
                                FilledTonalIconButton(onClick = viewModel::fetch) {
                                    Icon(
                                        Icons.Outlined.CloudDownload,
                                        contentDescription = "Télécharger"
                                    )
                                }
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Questions", fontWeight = FontWeight.SemiBold) },
                        supportingContent = {
                            val text =
                                if (homeState.totalQandasCount == 0) "No question yet."
                                else "${homeState.totalQandasCount} questions."
                            Text(text)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.QuestionAnswer,
                                contentDescription = "Available questions",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Quizzes", fontWeight = FontWeight.SemiBold) },
                        supportingContent = {
                            val text =
                                if (homeState.totalQuizCount == 0) "No quiz yet."
                                else "${homeState.totalQuizCount} quizzes."
                            Text(text)
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Quiz,
                                contentDescription = "Available quizzes",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }
}