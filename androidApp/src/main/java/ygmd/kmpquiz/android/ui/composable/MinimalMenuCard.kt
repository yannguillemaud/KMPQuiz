package ygmd.kmpquiz.android.ui.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MinimalMenuCard(
    title: String,
    subtitle: String = "",
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    isComingSoon: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(enabled = !isComingSoon) {
                if (!isComingSoon) {
                    isPressed = true
                    onClick()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isComingSoon)
                Color(0xFFF9FAFB)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isComingSoon) 1.dp else 2.dp
        ),
        shape = RoundedCornerShape(16.dp),
        border = if (!isComingSoon) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE5E7EB)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icône avec fond coloré
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isComingSoon)
                            Color(0xFFF3F4F6)
                        else
                            accentColor.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isComingSoon)
                        Color(0xFF9CA3AF)
                    else
                        accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Contenu textuel
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isComingSoon)
                                Color(0xFF9CA3AF)
                            else
                                Color(0xFF1F2937)
                        )
                    )

                    if (isComingSoon) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF3F4F6)
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Bientôt",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6B7280)
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = subtitle,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = if (isComingSoon)
                            Color(0xFFD1D5DB)
                        else
                            Color(0xFF6B7280)
                    )
                )
            }

            // Indicateur de navigation
            if (!isComingSoon) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color(0xFFF9FAFB),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
