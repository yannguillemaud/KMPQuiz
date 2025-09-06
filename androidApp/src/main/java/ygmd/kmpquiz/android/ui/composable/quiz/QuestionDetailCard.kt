package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.qanda.Qanda

@Composable
fun QuestionDetailCard(
    questionIndex: Int,
    qanda: Qanda,
    userAnswer: Choice?,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }
    val isCorrect = userAnswer == qanda.correctAnswer

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header avec numéro et statut
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${questionIndex + 1}",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF6B7280)
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = if (isCorrect) "Correct" else "Incorrect",
                            tint = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isCorrect) "Correct" else "Incorrect",
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isCorrect) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        )
                    }
                }

                // Question
                Text(
                    text = qanda.question.text,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937),
                        lineHeight = 22.sp
                    )
                )

                // Réponse utilisateur
                if (userAnswer != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Votre réponse",
                                tint = Color(0xFF6B7280),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Votre réponse : $userAnswer",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = if (isCorrect) Color(0xFF047857) else Color(0xFFB91C1C)
                                )
                            )
                        }
                    }
                }

                // Bonne réponse (si incorrect)
                if (!isCorrect) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF0FDF4)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lightbulb,
                                contentDescription = "Bonne réponse",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Bonne réponse : ${qanda.correctAnswer}",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF047857)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}