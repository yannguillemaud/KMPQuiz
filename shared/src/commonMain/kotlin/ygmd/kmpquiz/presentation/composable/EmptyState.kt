package ygmd.kmpquiz.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.presentation.theme.Dimens

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    description: String = "",
    fullSizeState: Boolean = false,
    onAction: (() -> Unit)? = null,
    actionLabel: String? = null,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (fullSizeState) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }

        Column(
            modifier = Modifier.padding(top = if (fullSizeState) Dimens.PaddingLarge else Dimens.PaddingSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
        ) {
            Text(
                text = title,
                style = if (fullSizeState) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                fontWeight = if (fullSizeState) FontWeight.Bold else null,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
            )

            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = if (fullSizeState) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
                )
            }

            if (onAction != null && actionLabel != null) {
                Box(modifier = Modifier.padding(top = Dimens.PaddingLarge)) {
                    Button(onClick = onAction) {
                        Text(text = actionLabel)
                    }
                }
            }
        }
    }
}
