package ygmd.kmpquiz.android.ui.composable.notificationsetting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.entities.cron.Cron
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.domain.entities.cron.CronPreset.DAILY

@Composable
fun CategoryNotificationCard(
    category: String,
    cron: Cron?,
    onSetCron: (CronPreset) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (cron != null) Color(0xFFF0F0FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = category,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937)
                        )
                    )
                    Text(
                        text = if (cron != null) "Configuration personnalisée" else "Utilise la configuration globale",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    )
                }

                if (cron != null) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (cron == null) {
                OutlinedButton(
                    onClick = { onSetCron(DAILY) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Personnaliser cette catégorie")
                }
            } else {
                CronSelector(
                    selectedCron = cron.expression,
                    onCronSelected = onSetCron,
                    label = "Fréquence pour cette catégorie"
                )
            }
        }
    }
}

fun CronExpression.asText(): String = when (toString()) {
    "0 0 * * *" -> "Tous les jours"
    "0 0 * * 0" -> "Toutes les semaines"
    "0 0 1 * *" -> "Tous les mois"
    "0 0 1 1 *" -> "Tous les ans"
    "0 * * * *" -> "Toutes les heures"
    else -> "Personnalisé"
}