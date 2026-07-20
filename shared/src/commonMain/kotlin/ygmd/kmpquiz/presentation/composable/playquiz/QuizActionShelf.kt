package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ygmd.kmpquiz.presentation.theme.Dimens

@Composable
fun QuizActionShelf(
    visible: Boolean,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.QuizActionShelfHeight),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically { it / 4 },
            exit = fadeOut(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            NextQuestionButton(onClick = onNext)
        }
    }
}
