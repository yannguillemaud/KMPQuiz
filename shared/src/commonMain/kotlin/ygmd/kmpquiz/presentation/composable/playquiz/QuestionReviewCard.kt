package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import ygmd.kmpquiz.presentation.composable.MediaImageView
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.UserAnswerState

/**
 * A single reviewed question: the question (text or image) followed by every answer choice
 * rendered non-interactively through [ChoiceItem], each tinted by its review state:
 * - the picked answer → [AnswerState.SelectedCorrect] / [AnswerState.SelectedWrong];
 * - the actual correct answer, shown quietly only on a wrong pick → [AnswerState.RevealedCorrect];
 * - everything else → [AnswerState.Unchosen].
 *
 * [ChoiceItem] is only interactive in its `Default` state; none of these states are `Default`,
 * so the choices are inert (no ripple) — the `onClick = {}` is never reached.
 */
@Composable
fun QuestionReviewCard(
    qanda: QandaUiState,
    userAnswer: UserAnswerState,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall),
        ) {
            when (val content = qanda.question) {
                is QandaUiState.QuestionUiState.TextQuestionUiState -> Text(
                    text = content.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                is QandaUiState.QuestionUiState.ImageQuestionUiState -> MediaImageView(
                    imageUrl = content.imageUrl,
                    contentDescription = content.altText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Fit,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Dimens.ChoiceVerticalPadding)) {
                qanda.answers.forEach { answer ->
                    val answerState = when {
                        answer.id == userAnswer.userAnswerContent.id && userAnswer.isCorrect ->
                            AnswerState.SelectedCorrect

                        answer.id == userAnswer.userAnswerContent.id ->
                            AnswerState.SelectedWrong

                        answer.id == userAnswer.correctAnswerContent.id && !userAnswer.isCorrect ->
                            AnswerState.RevealedCorrect

                        else -> AnswerState.Unchosen
                    }
                    ChoiceItem(
                        text = answer.text,
                        answerState = answerState,
                        onClick = {},
                    )
                }
            }
        }
    }
}
