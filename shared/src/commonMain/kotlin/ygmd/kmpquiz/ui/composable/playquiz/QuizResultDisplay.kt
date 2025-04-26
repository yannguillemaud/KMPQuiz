package ygmd.kmpquiz.ui.composable.playquiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ygmd.kmpquiz.domain.model.quiz.QuizResult
import ygmd.kmpquiz.ui.theme.Dimens
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@Composable
fun QuizResultDisplay(
    modifier: Modifier = Modifier,
    results: QuizResult,
    onFinished: () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(Dimens.CardElevation)
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.Companion
                        .padding(DefaultPadding)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Results", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.Companion.height(DefaultPadding))
                    Text("Questions: ${results.questions}", style = MaterialTheme.typography.bodyLarge)
                    Text("Score : ${results.score}", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        "Successrate : ${results.percentage}%",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.Companion.height(DefaultPadding * 2))
                    Button(
                        onClick = onFinished, modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text("Terminer")
                    }
                }
            }
        }

    }
}
