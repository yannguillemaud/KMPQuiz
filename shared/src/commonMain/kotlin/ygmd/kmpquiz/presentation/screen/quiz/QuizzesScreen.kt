package ygmd.kmpquiz.presentation.screen.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.core.domain.event.Event
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.composable.LoadingState
import ygmd.kmpquiz.core.service.permission.PermissionRationaleDialog
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.composable.playquiz.QuizCard
import ygmd.kmpquiz.presentation.theme.Dimens.BottomNavPadding
import ygmd.kmpquiz.presentation.theme.Dimens.PaddingMedium
import ygmd.kmpquiz.presentation.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.QuizzesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizzesScreen(
    quizViewModel: QuizViewModel = koinViewModel(),
    onNavigateToQuizEditor: (quizId: String?) -> Unit = {},
    onNavigateToPlayQuiz: (quizId: String) -> Unit = {},
    onNavigateToSessionHistory: () -> Unit = {}, // Incoming Feature
) {
    val quizzesUiState by quizViewModel.quizzesState.collectAsStateWithLifecycle(
        initialValue = QuizzesUiState(isLoading = true)
    )
    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(Unit) {
        quizViewModel.quizEvents.collect { event ->
            if (event is Event.ShowSnackbar) {
                snackbarHostState.showSnackbar(
                    message = event.message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Quizzes",
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    scrollBehavior = scrollBehavior
                )
                /** Incoming Feature
                PrimaryTabRow(selectedTabIndex = 0) {
                    Tab(
                        selected = true,
                        onClick = {},
                        text = { Text("Quizzes") }
                    )
                    Tab(
                        selected = false,
                        onClick = onNavigateToSessionHistory,
                        text = { Text("History") }
                    )
                }
                **/
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToQuizEditor(null) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Create New Quiz"
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PermissionRationaleDialog(handler = quizViewModel.notificationHandler)

            Crossfade(
                targetState = quizzesUiState,
                label = "QuizzesStateTransition",
                animationSpec = tween(durationMillis = 300)
            ) { state ->
                when {
                    state.isLoading -> {
                        LoadingState(modifier = Modifier.fillMaxSize())
                    }

                    state.error != null -> {
                        ErrorState(
                            modifier = Modifier.fillMaxSize(),
                            message = state.error,
                        )
                    }

                    state.quizzes.isEmpty() -> {
                        EmptyState(
                            modifier = Modifier.fillMaxSize(),
                            icon = Icons.Outlined.Quiz,
                            title = "No Quizzes Yet",
                            description = "Create your first quiz by tapping the + button below.",
                            fullSizeState = true
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = PaddingMedium,
                                end = PaddingMedium,
                                top = PaddingMedium,
                                bottom = BottomNavPadding
                            ),
                            verticalArrangement = Arrangement.spacedBy(PaddingMedium),
                        ) {
                            items(
                                items = state.quizzes,
                                key = { it.id }
                            ) { quiz ->
                                QuizCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    quiz = quiz,
                                    isEnabled = quiz.questionsSize > 0,
                                    onClick = { onNavigateToPlayQuiz(quiz.id) },
                                    onEdit = { onNavigateToQuizEditor(quiz.id) },
                                    onDelete = { quizViewModel.deleteQuiz(quiz.id) },
                                    onToggleCron = { isEnabled ->
                                        quizViewModel.toggleScheduler(quiz.id, isEnabled)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}