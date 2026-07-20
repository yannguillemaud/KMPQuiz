package ygmd.kmpquiz.presentation.composable.qanda

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import ygmd.kmpquiz.presentation.composable.MediaImageView

@Composable
fun ImageView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    MediaImageView(
        imageUrl = imageUrl,
        contentDescription = "Question image",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}