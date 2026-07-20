package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.theme.ExtendedTheme

sealed interface AnswerState {
    data object Default : AnswerState
    data object Selected : AnswerState
    data object SelectedCorrect : AnswerState
    data object SelectedWrong : AnswerState
    data object RevealedCorrect : AnswerState
    data object Unchosen : AnswerState
}

@Composable
fun ChoiceItem(
    text: String,
    answerState: AnswerState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val success = ExtendedTheme.colors.success
    val error = MaterialTheme.colorScheme.error
    val surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val onSurface = MaterialTheme.colorScheme.onSurface

    val containerColor by animateColorAsState(
        targetValue = when (answerState) {
            AnswerState.Default -> surfaceContainerHigh
            AnswerState.Selected -> MaterialTheme.colorScheme.primaryContainer
            AnswerState.SelectedCorrect -> success
            AnswerState.SelectedWrong -> error
            AnswerState.RevealedCorrect -> success.copy(alpha = 0.15f)
            AnswerState.Unchosen -> surfaceVariant.copy(alpha = 0.4f)
        },
        label = "choiceContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when (answerState) {
            AnswerState.Default -> onSurface
            AnswerState.Selected -> MaterialTheme.colorScheme.onPrimaryContainer
            AnswerState.SelectedCorrect -> Color.White
            AnswerState.SelectedWrong -> Color.White
            AnswerState.RevealedCorrect -> onSurface
            AnswerState.Unchosen -> onSurface.copy(alpha = 0.4f)
        },
        label = "choiceContentColor"
    )

    // Default outline stays a solid, animated color; feedback states get a gradient
    // border (glassmorphism accent) driven by success/error — the fill stays solid so
    // the correct/incorrect read is unambiguous.
    val defaultBorderColor by animateColorAsState(
        targetValue = if (answerState == AnswerState.Default) outline else Color.Transparent,
        label = "choiceBorderColor"
    )
    val successBorderBrush = remember(success) {
        Brush.linearGradient(listOf(success.copy(alpha = 0.9f), success.copy(alpha = 0.35f)))
    }
    val errorBorderBrush = remember(error) {
        Brush.linearGradient(listOf(error.copy(alpha = 0.9f), error.copy(alpha = 0.35f)))
    }
    val borderStroke = when (answerState) {
        AnswerState.Default -> BorderStroke(1.dp, SolidColor(defaultBorderColor))
        AnswerState.Selected -> BorderStroke(0.dp, SolidColor(Color.Transparent))
        AnswerState.SelectedCorrect,
        AnswerState.RevealedCorrect -> BorderStroke(2.dp, successBorderBrush)
        AnswerState.SelectedWrong -> BorderStroke(2.dp, errorBorderBrush)
        AnswerState.Unchosen -> BorderStroke(0.dp, SolidColor(Color.Transparent))
    }

    val isEnabled = answerState == AnswerState.Default

    // Brief scale-down on press for tactile feedback.
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "choicePressScale"
    )

    Card(
        onClick = onClick,
        enabled = isEnabled,
        interactionSource = interactionSource,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .defaultMinSize(minHeight = 64.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor,
        ),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingMediumSmall),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                color = contentColor,
                modifier = Modifier.weight(1f),
            )

            when (answerState) {
                AnswerState.SelectedCorrect -> {
                    Spacer(Modifier.width(Dimens.PaddingSmall))
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Correct",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                AnswerState.SelectedWrong -> {
                    Spacer(Modifier.width(Dimens.PaddingSmall))
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = "Wrong",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
                else -> Unit
            }
        }
    }
}
