package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NextQuestionButton(
    modifier: Modifier = Modifier,
    isQuizComplete: Boolean,
    onClick: () -> Unit
) {
    val buttonText = if (isQuizComplete) "Show results" else "Next question"
    val buttonIcon = if (isQuizComplete) Icons.Filled.DoneAll else Icons.AutoMirrored.Filled.ArrowForward

    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        AnimatedContent(
            targetState = Pair(buttonText, buttonIcon),
            transitionSpec = {
                (slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { width -> width / 2 }
                ) + fadeIn(animationSpec = tween(300)))
                    .togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { width -> -width / 2 }
                        ) + fadeOut(animationSpec = tween(300))
                    )
            },
            label = "NextButtonAnimation"
        ) { (text, icon) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier
                )
            }
        }
    }
}

@Preview
@Composable
fun NextQuestionButtonPreview() {
    NextQuestionButton(isQuizComplete = true, onClick = {})
}