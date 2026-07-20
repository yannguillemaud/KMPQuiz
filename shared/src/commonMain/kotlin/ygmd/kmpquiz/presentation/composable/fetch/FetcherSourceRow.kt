package ygmd.kmpquiz.presentation.composable.fetch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.theme.ExtendedTheme
import ygmd.kmpquiz.presentation.viewModel.home.FetcherStatus

/**
 * Available-sources row (Part B / Direction B1 — "status-forward source row").
 *
 * Re-skins a question source as a full-width list row that mirrors the Home quick-action
 * rows (leading tinted avatar, text block, trailing control) so the whole screen reads as
 * one calm stack of 56dp rows. A small status dot conveys state at a glance; the trailing
 * side is the Fetch/Retry control, or an inline slim progress bar while syncing.
 *
 * Theme/token safety: `secondary` stays the single card accent (leading avatar); the status
 * color is a small dot only, mapped to existing tokens (`onSurfaceVariant` / `secondary` /
 * `success` / `error`) so no new colors are introduced and contrast holds in both themes.
 */
@Composable
fun FetcherSourceRow(
    name: String,
    status: FetcherStatus,
    error: String?,
    onFetchInvoke: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSyncing = status == FetcherStatus.Syncing
    val hasError = status == FetcherStatus.Error

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = Dimens.ListRowMinHeight)
                    .padding(
                        horizontal = Dimens.PaddingMedium,
                        vertical = Dimens.PaddingSmall,
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
            ) {
                // Leading avatar: cloud glyph on a tinted circle (secondary register).
                Box(
                    modifier = Modifier
                        .size(Dimens.CategoryAvatarSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cloud,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(Dimens.SectionIconSize),
                    )
                }

                // Name + status chip.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Dimens.ChoiceVerticalPadding),
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                    )
                    SourceStatusChip(status = status)
                }

                // Trailing control: Fetch/Retry when idle, hidden while syncing (bar below).
                if (!isSyncing) {
                    FilledTonalButton(onClick = onFetchInvoke) {
                        Icon(
                            imageVector = if (hasError) Icons.Outlined.Refresh else Icons.Outlined.Download,
                            contentDescription = null,
                            modifier = Modifier.size(Dimens.SectionIconSize),
                        )
                        Text(
                            text = if (hasError) "Retry" else "Fetch",
                            modifier = Modifier.padding(start = Dimens.PaddingSmall),
                        )
                    }
                }
            }

            // Slim inline progress while syncing (indeterminate — the API gives no progress).
            if (isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}

/** Status dot + short label. Dot carries the accent; the label stays neutral (one accent). */
@Composable
private fun SourceStatusChip(status: FetcherStatus) {
    val label = when (status) {
        FetcherStatus.Ready -> "Ready"
        FetcherStatus.Syncing -> "Syncing"
        FetcherStatus.Updated -> "Updated"
        FetcherStatus.Error -> "Error"
    }
    val dotColor: Color = when (status) {
        FetcherStatus.Ready -> MaterialTheme.colorScheme.onSurfaceVariant
        FetcherStatus.Syncing -> MaterialTheme.colorScheme.secondary
        FetcherStatus.Updated -> ExtendedTheme.colors.success
        FetcherStatus.Error -> MaterialTheme.colorScheme.error
    }
    // Error is the one semantic exception where the label also takes the accent (M3 convention).
    val labelColor = if (status == FetcherStatus.Error) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.DotIndicatorSize)
                .clip(CircleShape)
                .background(dotColor),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = labelColor,
            maxLines = 1,
        )
    }
}
