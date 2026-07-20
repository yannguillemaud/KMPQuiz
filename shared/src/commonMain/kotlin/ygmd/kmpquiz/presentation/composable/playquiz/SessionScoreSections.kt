package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.core.domain.session.SessionStats
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.CategoryStatUiState
import kotlin.math.roundToInt

@Composable
fun GlobalScoreSection(stats: SessionStats, modifier: Modifier = Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.globalSuccessRate,
        animationSpec = tween(durationMillis = 1000),
        label = "ScoreAnimation"
    )

    Column(
        modifier = modifier.fillMaxWidth().padding(top = Dimens.PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(Dimens.ScoreDonutSize),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = Dimens.DonutStrokeWidth,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(Dimens.ScoreDonutSize),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = Dimens.DonutStrokeWidth,
                strokeCap = StrokeCap.Round
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(stats.globalSuccessRate * 100).roundToInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${stats.totalCorrect} / ${stats.totalQuestions} correct",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * A per-category score row. When [onClick] is non-null the whole card becomes tappable
 * (ripple + a trailing chevron hinting at drill-down into the category review); when null it
 * renders exactly as a static, non-interactive row.
 */
@Composable
fun CategoryStatRow(
    category: CategoryStatUiState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    if (onClick != null) {
        OutlinedCard(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            CategoryStatRowContent(category = category, showChevron = true)
        }
    } else {
        OutlinedCard(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            CategoryStatRowContent(category = category, showChevron = false)
        }
    }
}

@Composable
private fun CategoryStatRowContent(
    category: CategoryStatUiState,
    showChevron: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.categoryName.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${category.correctAnswers} / ${category.totalQuestions}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (showChevron) {
                Spacer(Modifier.width(Dimens.PaddingSmall))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Review category",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.IconSize),
                )
            }
        }

        LinearProgressIndicator(
            progress = { category.successRate },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(MaterialTheme.shapes.extraSmall),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.secondaryContainer,
            strokeCap = StrokeCap.Round
        )
    }
}
