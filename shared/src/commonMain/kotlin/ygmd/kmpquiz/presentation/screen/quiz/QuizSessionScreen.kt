package ygmd.kmpquiz.presentation.screen.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationEventHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.screen.quiz.content.QuizCompletedContent
import ygmd.kmpquiz.presentation.screen.quiz.content.QuizStartedContent
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionViewModel

@Composable
fun QuizSessionScreen(
    isNewSession: Boolean,
    quizId: String? = null,
    sessionId: String? = null,
    fromNotification: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onFinishQuiz: () -> Unit = {},
    onNavigateToCategoryReview: (sessionId: String, categoryId: String, categoryName: String) -> Unit = { _, _, _ -> },
    quizSessionViewModel: QuizSessionViewModel = koinViewModel(),
    // Whether this entry is genuinely the top of its tab's back stack right now, as opposed to
    // being composed only because NavDisplay is peeking it during a predictive-back gesture on a
    // route pushed on top of it (e.g. CategoryReview). Lifecycle state can't distinguish the two
    // (BackStackAwareLifecycleNavEntryDecorator resumes any entry still on the stack), so the
    // caller must pass this in from `navigator.state.currentEntry`. Defaults to `true` so other
    // call sites (previews, tests) are unaffected.
    isForeground: Boolean = true,
) {
    val quizUiState by quizSessionViewModel.sessionState.collectAsState()

    val transitionPhase by remember {
        derivedStateOf {
            when (quizUiState) {
                is QuizSessionUiState.Loading -> 0
                is QuizSessionUiState.Error -> 1
                is QuizSessionUiState.Completed -> 2
                is QuizSessionUiState.Idle -> 3
                is QuizSessionUiState.Started -> 4
            }
        }
    }

    LaunchedEffect(isNewSession) {
        // `quizId`/`sessionId` are nullable by the `PlaySession` route contract. Guard both the
        // same way (the resume branch already guarded `sessionId`) so a stray null can never crash
        // the session — in the (currently unreachable) new-session-without-quizId case we dispatch
        // nothing and the screen stays on its Loading state rather than NPE-ing.
        when {
            isNewSession && quizId != null ->
                quizSessionViewModel.processIntent(QuizSessionIntent.CreateSession(quizId))

            !isNewSession && sessionId != null ->
                quizSessionViewModel.processIntent(QuizSessionIntent.ResumeSession(sessionId))
        }
    }

    val backNavigationEventState = rememberNavigationEventState(NavigationEventInfo.None)
    NavigationEventHandler(
        state = backNavigationEventState,
        // Only claim back-handling precedence while this entry is truly foreground. Left at the
        // default `true`, this handler would still win a predictive-back gesture started from a
        // route pushed on top of it (e.g. CategoryReview) while NavDisplay peeks this content —
        // its "last composed + enabled wins" precedence rule doesn't otherwise distinguish a real
        // foreground entry from a peeked one. The composable call itself stays unconditional per
        // the API's own guidance: only vary isBackEnabled/isForwardEnabled, don't gate the call,
        // or composition order (and thus precedence) becomes unpredictable.
        isBackEnabled = isForeground,
        isForwardEnabled = false,
        onBackCompleted = {
            when (quizUiState) {
                is QuizSessionUiState.Completed -> onFinishQuiz()
                else -> onNavigateBack()
            }
        },
    )

    Scaffold { paddingValues ->
        Crossfade(
            targetState = transitionPhase,
            label = "QuizStateTransition",
            animationSpec = tween(durationMillis = 300),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { derivedQuizState ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    // Cap width first, while the incoming constraint from the wrapping Box is
                    // still loose, then fillMaxSize() expands into the now-capped max — the
                    // reverse order would be a no-op, since fillMaxSize() makes the constraint
                    // tight and a non-required widthIn(max) can only shrink a range that still
                    // has room to shrink.
                    modifier = Modifier
                        .widthIn(max = Dimens.SessionContentMaxWidth)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (derivedQuizState) {
                        0 -> CircularProgressIndicator()
                        1 -> ErrorState(message = (quizUiState as? QuizSessionUiState.Error)?.message ?: "Unknown error")
                        3 -> Button(onClick = { quizSessionViewModel.processIntent(QuizSessionIntent.StartSession) }) {
                            Text("Start Quiz")
                        }
                        // Started
                        4 -> {
                            var lastStarted by remember {
                                mutableStateOf(quizUiState as QuizSessionUiState.Started)
                            }
                            (quizUiState as? QuizSessionUiState.Started)?.let { lastStarted = it }
                            QuizStartedContent(
                                state = lastStarted,
                                fromNotification = fromNotification,
                                onNavigateBack = onNavigateBack,
                                onSubmitAnswer = { answerId ->
                                    quizSessionViewModel.processIntent(
                                        QuizSessionIntent.SubmitAnswer(answerId)
                                    )
                                },
                                onNextState = { quizSessionViewModel.processIntent(QuizSessionIntent.NextState) }
                            )
                        }
                        // Completed
                        2 -> {
                            (quizUiState as? QuizSessionUiState.Completed)?.let { completed ->
                                QuizCompletedContent(
                                    state = completed,
                                    onFinishQuiz = onFinishQuiz,
                                    onCategoryClick = { categoryId, categoryName ->
                                        onNavigateToCategoryReview(
                                            completed.sessionId,
                                            categoryId,
                                            categoryName
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


