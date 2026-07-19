package ygmd.kmpquiz.presentation.composable.playquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import ygmd.kmpquiz.presentation.theme.Dimens.PaddingMedium
import ygmd.kmpquiz.presentation.theme.Dimens.PaddingSmall

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(PaddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingSmall, Alignment.CenterVertically),
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
