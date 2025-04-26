package ygmd.kmpquiz.ui.composable.qanda

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ygmd.kmpquiz.ui.theme.Dimens.PaddingSmall

@Composable
fun PlayCategoryCard(
    modifier: Modifier = Modifier,
    category: String,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(PaddingSmall)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingSmall),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = category)
            }
        }
    }
}