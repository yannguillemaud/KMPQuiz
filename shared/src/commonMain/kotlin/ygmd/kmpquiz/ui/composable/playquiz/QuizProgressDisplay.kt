package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizSession
import ygmd.kmpquiz.ui.composable.qanda.ImageQuestionView
import ygmd.kmpquiz.ui.composable.qanda.PlayCategoryCard
import ygmd.kmpquiz.ui.theme.Dimens.PaddingSmall

@Composable
fun QuizInProgressDisplay(
    modifier: Modifier = Modifier,
    progressState: DisplayableQuizSession.InProgress,
    onSelectAnswer: (Choice) -> Unit,
) {
    val qanda = progressState.currentQanda
    val answers = progressState.shuffledAnswers
    val selectedAnswer = progressState.selectedAnswer

    Column(
        modifier = modifier.fillMaxSize()
            .padding(PaddingSmall),
        verticalArrangement = Arrangement.spacedBy(PaddingSmall)
    ) {
        PlayCategoryCard(category = qanda.category.name)
        PlayQuestionCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PaddingSmall),
            question = qanda.question,
            horizontalArrangement = Arrangement.Center
        )
        LazyColumn(verticalArrangement = Arrangement.SpaceBetween) {
            items(answers, key = { it.contextKey }){choice ->
                ChoiceItem(
                    choice = choice,
                    isCorrect = when {
                        selectedAnswer != null && choice == answers.correctAnswer -> true
                        selectedAnswer != null && choice == selectedAnswer && choice != answers.correctAnswer -> false
                        else -> null
                    },
                    enabled = selectedAnswer == null,
                    onClick = { onSelectAnswer(choice) },
                )
            }
        }
    }
}

@Composable
fun PlayQuestionCard(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    question: Question
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement
    ) {
        when (question) {
            is Question.TextQuestion -> {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
            }

            is Question.ImageQuestion -> {
                ImageQuestionView(question.imageUrl)
            }
        }
    }
}

