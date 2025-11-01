package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuiz
import ygmd.kmpquiz.ui.theme.Dimens
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding
import ygmd.kmpquiz.ui.theme.Dimens.PaddingSmall

@Composable
fun QuizCard(
    modifier: Modifier = Modifier,
    quiz: DisplayableQuiz,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onToggleCron: (Boolean) -> Unit = {},
) {
    val isCronSet = quiz.cron != null
    val isCronEnabled = isCronSet && quiz.cron.isEnabled

    val cardAlpha = if (isEnabled) 1f else 0.6f
    val cardOnClick = if (isEnabled) onClick else { {} }

    Card(
        modifier = modifier
            .alpha(cardAlpha)
            .clickable(onClick = cardOnClick, enabled = isEnabled),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(Dimens.CardElevation),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(DefaultPadding),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${quiz.questionsSize} questions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                Row {
                    Box {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                        }
                    }
                    Box {
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                        }
                    }
                    quiz.cron?.let {
                        Box {
                            Column(verticalArrangement = Arrangement.spacedBy(PaddingSmall)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Remind",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Switch(
                                        checked = isCronEnabled,
                                        enabled = isEnabled,
                                        onCheckedChange = { onToggleCron(it) }
                                    )
                                }
                                Text(
                                    text = it.cron.displayName,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
