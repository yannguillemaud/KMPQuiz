package ygmd.kmpquiz.presentation.composable.createquiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableQuizMode

@Composable
fun QuizQuestionConfigSection(
    quizMode: DisplayableQuizMode,
    onModeSelected: (DisplayableQuizMode) -> Unit,
    onGlobalLimitChanged: (Int) -> Unit,
    onCategoryLimitChanged: (String, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val modes = listOf(
                Triple("Full", Icons.Default.AllInclusive, 0),
                Triple("Limited", Icons.Default.FormatListNumbered, 1),
                Triple("By Category", Icons.Default.Category, 2)
            )
            modes.forEachIndexed { index, (label, icon, _) ->
                val isSelected = when (index) {
                    0 -> quizMode is DisplayableQuizMode.Full
                    1 -> quizMode is DisplayableQuizMode.Limited
                    else -> quizMode is DisplayableQuizMode.ByCategory
                }
                SegmentedButton(
                    selected = isSelected,
                    onClick = {
                        val newMode = when (index) {
                            0 -> DisplayableQuizMode.Full
                            1 -> DisplayableQuizMode.Limited(0, 50)
                            else -> DisplayableQuizMode.ByCategory()
                        }
                        onModeSelected(newMode)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                    icon = {
                        SegmentedButtonDefaults.Icon(active = isSelected) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    }
                ) {
                    Text(label, style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        AnimatedContent(
            targetState = quizMode,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "quizModeContent"
        ) { targetMode ->
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (targetMode) {
                    is DisplayableQuizMode.Limited -> {
                        LimitItem(
                            key = targetMode,
                            label = "Overall Question Limit",
                            value = targetMode.limit,
                            max = targetMode.max
                        ) { onGlobalLimitChanged(it) }
                    }

                    is DisplayableQuizMode.ByCategory -> {
                        val limits = targetMode.limits
                        if (limits.isEmpty()) {
                            EmptyState(
                                modifier = Modifier.fillMaxWidth(),
                                icon = Icons.Default.Category,
                                title = "No Categories Selected",
                                description = "Please select at least one category to include qandas in this quiz."
                            )
                        } else limits.forEach { (cat, current) ->
                            LimitItem(
                                key = cat.id to targetMode,
                                label = cat.name,
                                value = current,
                                max = cat.questionsCount
                            ) { onCategoryLimitChanged(cat.id, it) }
                        }
                    }

                    else -> {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AllInclusive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "All available questions from selected categories will be included in this quiz.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Enhanced slider item with a subtle container to prevent "fusion" with other sliders.
 */
@Composable
private fun LimitItem(
    key: Any,
    label: String,
    value: Int,
    max: Int,
    onValueChangeFinished: (Int) -> Unit
) {
    var localValue by remember(key) { mutableFloatStateOf(value.toFloat()) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${localValue.toInt()} / $max",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Slider(
                value = localValue,
                onValueChange = { localValue = it },
                valueRange = 0f..max.toFloat().coerceAtLeast(1f),
                modifier = Modifier.padding(top = 8.dp),
                onValueChangeFinished = {
                    onValueChangeFinished(localValue.toInt())
                }
            )
        }
    }
}

