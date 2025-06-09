package ygmd.kmpquiz.android.ui.composable.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsCard(
    qandasPlayedCount: Int,
    qandasSavedCount: Int,
    qandasScheduledCount: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Aperçu",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStat(
                    label = "Quiz joués",
                    value = "$qandasPlayedCount",
                    color = Color(0xFF4F46E5)
                )
                QuickStat(
                    label = "Quiz sauvegardés",
                    value = "$qandasSavedCount",
                    color = Color(0xFF10B981)
                )
                QuickStat(
                    label = "Quiz programmés",
                    value = "$qandasScheduledCount",
                    color = Color(0xFFF59E0B)
                )
            }
        }
    }
}

@Composable
fun QuickStat(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            ),
            textAlign = TextAlign.Center
        )
    }
}