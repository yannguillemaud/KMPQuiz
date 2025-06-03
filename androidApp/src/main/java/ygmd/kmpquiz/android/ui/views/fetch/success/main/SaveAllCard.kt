package ygmd.kmpquiz.android.ui.views.fetch.success.main

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SaveAllSection(
    onSaveAll: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(modifier = modifier) {
        Button(onClick = { onSaveAll() }) {
            Text("Save All")
        }
    }
}