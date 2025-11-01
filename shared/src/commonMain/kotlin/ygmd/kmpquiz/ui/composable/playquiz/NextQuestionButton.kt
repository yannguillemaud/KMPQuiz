package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable


@Composable
fun NextQuestionButton(onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = "Question suivante" // Bon pour l'accessibilité
        )
    }
}