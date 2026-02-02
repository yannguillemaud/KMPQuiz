package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.model.qanda.Choice
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.ui.composable.qanda.ImageView

@Composable
fun QandaQuizCard(
    qanda: DisplayableQanda,
    selectedAnswer: Choice?,
    onSelectAnswer: (Choice) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // IMAGE CARD
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = qanda.category.name,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Card(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (qanda.question is Question.ImageQuestion) {
                    ImageView(imageUrl = qanda.question.imageUrl, modifier = Modifier.fillMaxSize())
                } else if (qanda.question is Question.TextQuestion) {
                    Text(
                        text = qanda.question.text,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // GRID DE RÉPONSES
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            qanda.answers.chunked(2).forEach { rowChoices ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowChoices.forEach { choice ->
                        val isSelected = selectedAnswer == choice
                        val isCorrect = choice == qanda.answers.correctAnswer
                        val hasAnswered = selectedAnswer != null

                        val borderColor = when {
                            !hasAnswered -> MaterialTheme.colorScheme.outline
                            isSelected && isCorrect -> Color(0xFF4CAF50)
                            isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                            !isSelected && isCorrect -> Color(0xFF4CAF50)
                            else -> Color.Transparent
                        }

                        val containerColor = when {
                            !hasAnswered -> MaterialTheme.colorScheme.surfaceContainerHigh
                            isSelected && isCorrect -> Color(0xFFE8F5E9)
                            isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        }

                        AnswerButton(
                            text = choice.contextKey,
                            modifier = Modifier.weight(1f),
                            enabled = !hasAnswered,
                            onClick = { onSelectAnswer(choice) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = containerColor,
                                contentColor = if (hasAnswered && !isSelected && !isCorrect)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = containerColor,
                                disabledContentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(
                                width = if (isSelected || (hasAnswered && isCorrect)) 3.dp else 1.dp,
                                color = borderColor
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    border: BorderStroke? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = colors,
        border = border,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(16.dp),
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}