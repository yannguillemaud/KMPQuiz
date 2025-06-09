package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.foundation.layout.Box
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
fun QuestionCard(question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp) // Cohérent avec ton style
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp), // Respiration généreuse
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question,
                style = TextStyle(
                    fontSize = 20.sp, // Plus grand pour la lisibilité
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937),
                    lineHeight = 28.sp, // Interlignage pour longues questions
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}