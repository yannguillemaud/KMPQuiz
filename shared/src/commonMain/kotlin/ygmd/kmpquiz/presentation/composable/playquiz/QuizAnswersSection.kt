package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState

@Composable
fun QuizAnswersSection(
    answers: List<QandaUiState.AnswerUiState>,
    selectedAnswerId: String?,
    correctAnswerId: String,
    onSelectAnswer: (String) -> Unit,
    modifier: Modifier = Modifier,
    revealFeedback: Boolean = true,
) {
    // Once the user has answered, render true feedback using the domain-sourced
    // correctAnswerId: the pick is SelectedCorrect/SelectedWrong, and on a wrong pick
    // the right answer reveals itself (RevealedCorrect). Everything else dims (Unchosen).
    val hasAnswered = selectedAnswerId != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall),
    ) {
        answers.forEach { answer ->
            val answerState = when {
                !hasAnswered -> AnswerState.Default
                !revealFeedback && answer.id == selectedAnswerId -> AnswerState.Selected
                !revealFeedback -> AnswerState.Unchosen
                answer.id == selectedAnswerId && answer.id == correctAnswerId -> AnswerState.SelectedCorrect
                answer.id == selectedAnswerId -> AnswerState.SelectedWrong
                answer.id == correctAnswerId -> AnswerState.RevealedCorrect
                else -> AnswerState.Unchosen
            }

            ChoiceItem(
                text = answer.text,
                answerState = answerState,
                onClick = { onSelectAnswer(answer.id) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
