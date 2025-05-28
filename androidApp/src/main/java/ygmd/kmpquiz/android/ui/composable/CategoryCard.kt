package ygmd.kmpquiz.android.ui.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.android.model.DownloadedUiModel
import ygmd.kmpquiz.android.model.FetchedQandaModel

@Composable
fun FetchedQandaCard(
    qanda: FetchedQandaModel,
    modifier: Modifier = Modifier,
    status: DownloadedUiModel,
    onClick: ((FetchedQandaModel) -> Unit)? = null,
    onSaveClick: ((FetchedQandaModel) -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick(qanda) }
                } else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Question
            Text(
                text = qanda.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Catégorie et difficulté
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Catégorie
                CategoryTag(category = qanda.category)

                // Difficulté
                DifficultyTag(difficulty = qanda.difficultyProperties)
            }

            // Statut
            StatusIndicator(
                status = status,
                onClick = { onSaveClick?.invoke(qanda) }
            )
        }
    }
}

@Composable
private fun CategoryTag(category: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.wrapContentSize(),
    ) {
        Text(
            text = category,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium,
        )
    }
}


@Composable
private fun DifficultyTag(difficulty: DifficultyProperties) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = difficulty.color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, difficulty.color),
        modifier = Modifier.wrapContentSize()
    ) {
        Text(
            text = difficulty.displayName,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = difficulty.color,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun StatusIndicator(
    status: DownloadedUiModel,
    onClick: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, color = Color.Black),
            modifier = Modifier
                .wrapContentSize()
                .then(
                    if (onClick != null)
                        Modifier.clickable { onClick() }
                    else Modifier)
        ) {
            Icon(
                imageVector = status.icon,
                contentDescription = status.displayName,
                tint = status.color,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = status.color,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

sealed class DifficultyProperties(val displayName: String, val color: Color) {
    data object EASY : DifficultyProperties("Easy", Color(0xFF4CAF50))
    data object MEDIUM : DifficultyProperties("Medium", Color(0xFFFF9800))
    data object HARD : DifficultyProperties("Hard", Color(0xFFF44336))
    data object UNKNOWN : DifficultyProperties("Unknown", Color.Gray)
}


// Preview
@Preview(showBackground = true)
@Composable
fun QandaCardPreview() {


}