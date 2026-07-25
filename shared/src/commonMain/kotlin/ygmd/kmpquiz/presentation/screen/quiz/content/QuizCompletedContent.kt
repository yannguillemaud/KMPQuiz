package ygmd.kmpquiz.presentation.screen.quiz.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ygmd.kmpquiz.presentation.composable.playquiz.CategoryStatRow
import ygmd.kmpquiz.presentation.composable.playquiz.GlobalScoreSection
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QuizSessionUiState
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun QuizCompletedContent(
    state: QuizSessionUiState.Completed,
    onFinishQuiz: () -> Unit,
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            var heroRisen by remember { mutableStateOf(false) }
            var revealedCount by remember { mutableIntStateOf(0) }
            // The hero card starts centred (35% down) and rises to the top before the
            // score sections reveal one-by-one underneath it.
            val topSpace by animateDpAsState(
                targetValue = if (heroRisen) 0.dp else maxHeight * 0.35f,
                animationSpec = tween(650, easing = FastOutSlowInEasing),
                label = "heroRise",
            )
            val statCount = 1 + state.categoriesBreakdown.size
            LaunchedEffect(Unit) {
                heroRisen = true
                delay(650.milliseconds)
                repeat(statCount) { i ->
                    delay(120.milliseconds)
                    revealedCount = i + 1
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(topSpace))
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(72.dp),
                )
                Spacer(Modifier.height(Dimens.PaddingSmall))
                Text(
                    text = "Quiz completed",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(Dimens.PaddingLarge))
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall),
                    contentPadding = PaddingValues(bottom = Dimens.PaddingLarge),
                ) {
                    item {
                        AnimatedVisibility(
                            visible = revealedCount >= 1,
                            enter = fadeIn() + slideInVertically { it / 4 },
                        ) {
                            GlobalScoreSection(stats = state.stats)
                        }
                    }
                    itemsIndexed(state.categoriesBreakdown) { idx, cat ->
                        AnimatedVisibility(
                            visible = revealedCount >= idx + 2,
                            enter = fadeIn() + slideInVertically { it / 4 },
                        ) {
                            CategoryStatRow(
                                category = cat,
                                onClick = { onCategoryClick(cat.categoryId, cat.categoryName) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
        IconButton(
            onClick = onFinishQuiz,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = Dimens.PaddingSmall),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
