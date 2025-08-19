package ygmd.kmpquiz.android.ui.views.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizIntent
import ygmd.kmpquiz.viewModel.quiz.QuizViewModel
import ygmd.kmpquiz.viewModel.quiz.QuizzesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    onCreateQuiz: () -> Unit,
    onStartQuiz: (String) -> Unit,
    viewModel: QuizViewModel = koinViewModel()
) {
    val state by viewModel.quizzesState.collectAsState(QuizzesUiState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateQuiz
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter quiz")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.quizzes.values.toList()) { quiz ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                            .clickable(
                                enabled = quiz.qandas.isNotEmpty(),
                                onClick = { onStartQuiz(quiz.id) }
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(quiz.title, style = MaterialTheme.typography.titleMedium)
                            Text("${quiz.qandas.size} questions", style = MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = {
                                viewModel.processIntent(QuizIntent.DeleteQuiz(quiz.id))
                            }
                        ) {
                            Text("Supprimer")
                        }
                    }
                }
            }
        }
    }
}