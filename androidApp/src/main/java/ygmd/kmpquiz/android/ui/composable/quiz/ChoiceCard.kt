package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.entities.qanda.Choice

@Composable
fun ChoiceCard(
    choice: Choice,
    isSelected: Boolean,
    isAnswered: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    // Logique des états visuels
    val (backgroundColor, borderColor, textColor) = when {
        isAnswered && isCorrect == true -> Triple(
            Color(0xFFF0FDF4), // Vert très clair
            Color(0xFF10B981), // Vert
            Color(0xFF047857)  // Vert foncé
        )

        isAnswered && isCorrect == false && isSelected -> Triple(
            Color(0xFFFEF2F2), // Rouge très clair
            Color(0xFFEF4444), // Rouge
            Color(0xFFB91C1C)  // Rouge foncé
        )

        isSelected && !isAnswered -> Triple(
            Color(0xFFF0F0FF), // Bleu très clair
            Color(0xFF4F46E5), // Bleu primaire
            Color(0xFF3730A3)  // Bleu foncé
        )

        else -> Triple(
            Color.White,
            Color(0xFFE5E7EB), // Gris clair
            Color(0xFF374151)  // Gris foncé
        )
    }

    val animatedBorderColor by animateColorAsState(
        targetValue = borderColor,
        animationSpec = tween(200),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isAnswered) { onClick() }, // Désactivé après réponse
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = if (isSelected || isCorrect == true) 2.dp else 1.dp,
            color = animatedBorderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            when (choice) {
                is Choice.TextChoice -> Text(
                    text = choice.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                        color = textColor,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.weight(1f)
                )

                is Choice.ImageChoice -> {
                    /* TODO */
                }
            }
        }

        when {
            isAnswered && isCorrect == true -> Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Correct",
                tint = Color(0xFF10B981),
                modifier = Modifier.size(24.dp)
            )

            isAnswered && isCorrect == false && isSelected -> Icon(
                imageVector = Icons.Filled.Cancel,
                contentDescription = "Incorrect",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(24.dp)
            )

            isSelected && !isAnswered -> Icon(
                imageVector = Icons.Filled.RadioButtonChecked,
                contentDescription = "Sélectionné",
                tint = Color(0xFF4F46E5),
                modifier = Modifier.size(20.dp)
            )

            else -> Icon(
                imageVector = Icons.Filled.RadioButtonUnchecked,
                contentDescription = "Non sélectionné",
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}