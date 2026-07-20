package ygmd.kmpquiz.presentation.screen.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.presentation.composable.playquiz.CategoryStatRow
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.composable.playquiz.GlobalScoreSection
import ygmd.kmpquiz.presentation.composable.playquiz.QuizActionShelf
import ygmd.kmpquiz.presentation.composable.playquiz.QuizAnswersSection
import ygmd.kmpquiz.presentation.composable.playquiz.QuizHeroQuestionCard
import ygmd.kmpquiz.presentation.composable.playquiz.QuizInlineHeader
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalComposeUiApi::class)
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

    BackHandler {
        when (quizUiState) {
            is QuizSessionUiState.Completed -> onFinishQuiz()
            else -> onNavigateBack()
        }
    }

    Scaffold { paddingValues ->
        Crossfade(
            targetState = transitionPhase,
            label = "QuizStateTransition",
            animationSpec = tween(durationMillis = 300),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { derivedQuizState ->
            Column(
                modifier = Modifier.fillMaxSize(),
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
                            onNextState = { quizSessionViewModel.processIntent(QuizSessionIntent.NextState)}
                        )
                    }
                    // Completed
                    2 -> {
                        (quizUiState as? QuizSessionUiState.Completed)?.let { completed ->
                            QuizCompletedContent(
                                state = completed,
                                onFinishQuiz = onFinishQuiz,
                                onCategoryClick = { categoryId, categoryName ->
                                    onNavigateToCategoryReview(completed.sessionId, categoryId, categoryName)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizStartedContent(
    state: QuizSessionUiState.Started,
    fromNotification: Boolean,
    onNavigateBack: () -> Unit,
    onSubmitAnswer: (answerId: String) -> Unit,
    onNextState: () -> Unit,
) {
    // Local transient UI state: tracks which answer the user tapped before advancing.
    // This is presentation-only and must not leak into the ViewModel.
    var selectedAnswerId by remember(state.index) { mutableStateOf<String?>(null) }

    val isLastQuestion = state.isLastQuestion

    // On the last question there is no "Next" button: answering auto-finishes the quiz
    // after a short pause so the user can register the correct/incorrect feedback first.
    LaunchedEffect(selectedAnswerId) {
        if (selectedAnswerId != null && (fromNotification || isLastQuestion)) {
            delay((if (fromNotification) 700 else 1200).milliseconds)
            onNextState()
        }
    }

    // Animated linear progress: smoothly fills as the question index advances.
    val rawProgress = if (state.questionsCount > 0) {
        state.index.toFloat() / state.questionsCount.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        label = "quizProgress",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
    ) {
        QuizInlineHeader(
            categoryName = state.currentQanda.category,
            currentIndex = state.index + 1,
            total = state.questionsCount,
            onNavigateBack = onNavigateBack,
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(Modifier.height(Dimens.PaddingSmall))

        QuizHeroQuestionCard(
            questionContent = state.currentQanda.question,
            modifier = Modifier.weight(1f),
        )

        Spacer(Modifier.height(Dimens.PaddingSmall))

        QuizAnswersSection(
            answers = state.currentQanda.answers,
            selectedAnswerId = selectedAnswerId,
            correctAnswerId = state.currentQanda.correctAnswerId,
            onSelectAnswer = { id ->
                if (selectedAnswerId == null) {
                    selectedAnswerId = id
                    onSubmitAnswer(id)
                }
            },
            revealFeedback = !fromNotification,
        )

        // Permanent, fixed-height action shelf. It always occupies the same vertical
        // space, so the hero card (weight(1f)) and the answers never reflow when the
        // button appears — the button is only faded/slid in inside the reserved box.
        QuizActionShelf(
            visible = selectedAnswerId != null && !isLastQuestion && !fromNotification,
            onNext = onNextState,
        )

        Spacer(Modifier.height(Dimens.PaddingSmall))
    }
}

@Composable
private fun QuizCompletedContent(
    state: QuizSessionUiState.Completed,
    onFinishQuiz: () -> Unit,
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            var heroRisen by remember { mutableStateOf(false) }
            var revealedCount by remember { mutableIntStateOf(0) }
            // The hero card starts centred (35% down) and rises to the top before the
            // score sections reveal one-by-one underneath it.
            val topSpace by animateDpAsState(
                targetValue = if (heroRisen) 0.dp else maxHeight * 0.35f,
                animationSpec = tween(650, easing = FastOutSlowInEasing),
                label = "heroRise",
            )
            val statCount = 1 + state.categoriesBreakdown.size
            LaunchedEffect(Unit) {
                heroRisen = true
                delay(650.milliseconds)
                repeat(statCount) { i ->
                    delay(120.milliseconds)
                    revealedCount = i + 1
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(topSpace))
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(Modifier.height(Dimens.PaddingSmall))
                Text(
                    text = "Quiz completed",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Dimens.PaddingLarge))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall),
                    contentPadding = PaddingValues(bottom = Dimens.PaddingLarge),
                ) {
                    item {
                        AnimatedVisibility(
                            visible = revealedCount >= 1,
                            enter = fadeIn() + slideInVertically { it / 4 },
                        ) {
                            GlobalScoreSection(stats = state.stats)
                        }
                    }
                    itemsIndexed(state.categoriesBreakdown) { idx, cat ->
                        AnimatedVisibility(
                            visible = revealedCount >= idx + 2,
                            enter = fadeIn() + slideInVertically { it / 4 },
                        ) {
                            CategoryStatRow(
                                category = cat,
                                onClick = { onCategoryClick(cat.categoryId, cat.categoryName) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onFinishQuiz,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = Dimens.PaddingSmall),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
