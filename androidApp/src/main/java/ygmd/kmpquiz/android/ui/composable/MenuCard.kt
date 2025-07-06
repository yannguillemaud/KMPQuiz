package ygmd.kmpquiz.android.ui.composable

import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MenuCard(
    title: String,
    onNavigateTo: () -> Unit,
) {
    Button(
        onClick = onNavigateTo,
    ) {
        IconButton(onNavigateTo) {
            Text(title)
        }
    }
}