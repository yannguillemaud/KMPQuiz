package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.QuizViewModel

@Composable
fun QuizzesScreen(
    quizViewModel: QuizViewModel = koinViewModel()
){
    Text("Quiz")
}