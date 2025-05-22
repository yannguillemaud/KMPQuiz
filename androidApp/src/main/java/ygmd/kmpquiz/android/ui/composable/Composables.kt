package ygmd.kmpquiz.android.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.pojo.Quiz

@Composable
fun QuizList(list: List<Quiz>, block: @Composable (quiz: Quiz) -> Unit){
    LazyColumn {
        list.forEach { item { block(it) } }
    }
}

@Composable
fun QandaList(
    qandas: List<QANDA>,
    modifier: Modifier,
    padding: PaddingValues = PaddingValues(horizontal = 16.dp),
    itemSpacing: Dp = 8.dp,
    filter: (QANDA) -> Boolean = { true },
    block: @Composable (QANDA) -> Unit
){
    LazyColumn(
        modifier = modifier,
        contentPadding = padding,
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(qandas.filter(filter)){
            block(it)
        }
    }
}


@Composable
fun QandaComposable(qanda: QANDA) {
    val question = qanda.question
    val answers = qanda.answers
    val correctAnswer = qanda.correctAnswer

    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        answers.forEach { answer ->
            val isCorrect = selectedAnswer != null && answer == correctAnswer
            val isSelected = selectedAnswer == answer

            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(enabled = selectedAnswer == null) {
                        selectedAnswer = answer
                    },
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSelected && isCorrect -> Color(0xFFC8E6C9) // green
                        isSelected && !isCorrect -> Color(0xFFFFCDD2) // red
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp
                    )
                }
            }
        }

        if (selectedAnswer != null) {
            Spacer(modifier = Modifier.height(16.dp))
            val resultText = if (selectedAnswer == correctAnswer) {
                "Bonne réponse !"
            } else {
                "Mauvaise réponse. La bonne réponse était : $correctAnswer"
            }
            Text(
                text = resultText,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (selectedAnswer == correctAnswer) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }
    }
}