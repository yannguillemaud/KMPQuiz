package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.presentation.composable.MediaImageView
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.category.DisplayableQanda

@Composable
fun QandaQuizCard(
    qanda: DisplayableQanda,
    selectedAnswer: AnswerContent?,
    onSelectAnswer: (AnswerContent) -> Unit,
) {
    val chunkedAnswers = remember(qanda.answers) { qanda.answers.chunked(2) }
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.PaddingMedium)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = qanda.category.name,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .defaultMinSize(minHeight = Dimens.PaddingExtraLarge * 5),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            radius = 800f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (qanda.question) {
                    is QuestionContent.ImageContent -> {
                        MediaImageView(
                            imageUrl = qanda.question.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = Dimens.ImageAnswerHeight * 2 + Dimens.PaddingLarge),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    is QuestionContent.TextContent -> {
                        Text(
                            text = qanda.question.text,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(Dimens.PaddingLarge),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(Dimens.PaddingLarge))

        Column(verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)) {
            chunkedAnswers.forEach { rowChoices ->
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)) {
                    rowChoices.forEach { choice ->
                        val isSelected = selectedAnswer == choice
                        val isCorrect = choice == qanda.answers.correctAnswer
                        val hasAnswered = selectedAnswer != null

                        val answerState = when {
                            !hasAnswered -> AnswerState.Default
                            isSelected && isCorrect -> AnswerState.SelectedCorrect
                            isSelected && !isCorrect -> AnswerState.SelectedWrong
                            !isSelected && isCorrect -> AnswerState.RevealedCorrect
                            else -> AnswerState.Unchosen
                        }

                        ChoiceItem(
                            text = choice.contextKey,
                            answerState = answerState,
                            onClick = { if (!hasAnswered) onSelectAnswer(choice) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
