package ygmd.kmpquiz.presentation.composable.qanda

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import ygmd.kmpquiz.presentation.theme.Dimens

/**
 * Full-screen dialog that displays a Q&A question image at large size.
 *
 * Layered as: (1) a lightly-tinted, strongly-transparent scrim that keeps the
 * underlying screen clearly visible and dismisses on tap, and (2) a bordered
 * "box" (surface card) centered on top of it, holding [altText] as a bold
 * title above the image. The image is rendered with [ContentScale.Fit] and no
 * forced size, so small thumbnails stay sharp and are never upscaled.
 *
 * Dismissal happens only by tapping the scrim outside the box, or via system
 * back (handled by [Dialog]'s onDismissRequest) — there is no explicit close
 * button; taps on the box itself are consumed so they never dismiss.
 *
 * @param imageUrl remote/local image model passed to Coil's [AsyncImage].
 * @param altText optional caption shown as the box title; also used as the
 *   accessibility label for the image.
 * @param onDismiss invoked on scrim tap or system back.
 */
@Composable
fun QandaImagePreview(
    imageUrl: String,
    altText: String? = null,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                .clickable(onClick = onDismiss)
                .padding(Dimens.PaddingLarge),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.large
                    )
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        shape = MaterialTheme.shapes.large
                    )
                    .clip(MaterialTheme.shapes.large)
                    .padding(Dimens.PaddingMedium)
                    // Consume taps on the box so they don't reach the scrim's
                    // dismiss clickable behind it.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = altText ?: "Image question",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = Dimens.PaddingMediumSmall)
                )

                AsyncImage(
                    model = imageUrl,
                    contentDescription = altText ?: "Question image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}
