package ygmd.kmpquiz.android.ui.views.fetch.success

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.repository.DraftQanda

@Composable
fun GroupedDraftQandasCard(
    identifier: String,
    qandas: List<DraftQanda>,
    onSaveAction: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.3f))
            )
            .clickable(
                onClick = onSaveAction
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = identifier,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Text(
                text = "${qandas.size} questions",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}