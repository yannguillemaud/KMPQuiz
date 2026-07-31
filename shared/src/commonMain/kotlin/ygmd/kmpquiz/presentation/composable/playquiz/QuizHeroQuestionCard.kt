package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.presentation.composable.MediaImageView
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState

@Composable
fun QuizHeroQuestionCard(
    questionContent: QandaUiState.QuestionUiState,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    // Soft glassmorphism-as-accent gradient fill (no real backdrop blur in KMP commonMain).
    val accentBrush = remember(primaryColor, secondaryColor) {
        Brush.linearGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.08f),
                secondaryColor.copy(alpha = 0.04f),
                Color.Transparent,
            )
        )
    }
    // Subtle alpha-band gradient border to lift the card without shadow.
    val borderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.08f),
            )
        )
    }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            // Non-required: intersects with the incoming constraint rather than overriding
            // it, so this only takes effect because the call site (QuizStartedContent)
            // wraps this card in Box(Modifier.weight(1f), contentAlignment = Center) instead
            // of putting weight(1f) directly here. See Dimens.HeroCardMinHeight's KDoc.
            .heightIn(min = Dimens.HeroCardMinHeight, max = Dimens.HeroCardMaxHeight)
            .border(
                width = 1.dp,
                brush = borderBrush,
                shape = MaterialTheme.shapes.extraLarge,
            ),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = Dimens.CardElevation),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(accentBrush),
            contentAlignment = Alignment.Center,
        ) {
            // Each new question enters with a fade + slight upward slide, keyed on the
            // question content (data-class equality ⇒ only animates on real change).
            AnimatedContent(
                targetState = questionContent,
                transitionSpec = {
                    (fadeIn() + slideInVertically { it / 8 }) togetherWith fadeOut()
                },
                label = "heroQuestionEntrance",
            ) { content ->
                when (content) {
                    is QandaUiState.QuestionUiState.ImageQuestionUiState -> {
                        MediaImageView(
                            imageUrl = content.imageUrl,
                            contentDescription = content.altText,
                            // The card itself is already bounded (Dimens.HeroCardMinHeight /
                            // HeroCardMaxHeight on the ElevatedCard above), so the image just
                            // fills whatever space the card ends up with — no separate cap
                            // needed here.
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(Dimens.PaddingMedium),
                            contentScale = ContentScale.Fit,
                        )
                    }

                    is QandaUiState.QuestionUiState.TextQuestionUiState -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = content.text,
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
        }
    }
}
