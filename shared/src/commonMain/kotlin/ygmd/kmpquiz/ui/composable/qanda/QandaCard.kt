package ygmd.kmpquiz.ui.composable.qanda

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter


@Composable
fun ImageView(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(imageUrl)
    val state by painter.state.collectAsState()

    when (state) {
        AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .padding(16.dp)
            )
        }

        is AsyncImagePainter.State.Error -> {
            Text(
                text = "Erreur de chargement de l'image",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        is AsyncImagePainter.State.Success -> {
            Image(
                modifier = modifier,
                painter = painter,
                contentDescription = "Image de la question",
                contentScale = ContentScale.Fit
            )
        }
    }
}