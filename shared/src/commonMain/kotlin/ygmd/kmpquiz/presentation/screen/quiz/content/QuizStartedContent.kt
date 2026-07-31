package ygmd.kmpquiz.presentation.screen.quiz.content

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import ygmd.kmpquiz.presentation.composable.playquiz.QuizActionShelf
import ygmd.kmpquiz.presentation.composable.playquiz.QuizAnswersSection
import ygmd.kmpquiz.presentation.composable.playquiz.QuizHeroQuestionCard
import ygmd.kmpquiz.presentation.composable.playquiz.QuizInlineHeader
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionUiState
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun QuizStartedContent(
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

        // weight(1f) lives on this wrapping Box, not on the card itself: Box's default
        // propagateMinConstraints = false stops it forwarding a tight incoming min height to
        // the card, letting the card's own heightIn(min = HeroCardMinHeight, max =
        // HeroCardMaxHeight) act as a genuine best-effort clamp instead of a hard floor that
        // could overflow past this Column's bounds on a short window (see
        // Dimens.HeroCardMinHeight's KDoc).
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            QuizHeroQuestionCard(
                questionContent = state.currentQanda.question,
            )
        }

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
