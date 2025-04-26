package ygmd.kmpquiz.service.models

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import ygmd.kmpquiz.domain.Quiz

// TODO Koin as DI for viewmodel
abstract class QuizViewModel : ViewModel(){
    abstract val quizzesFlow: StateFlow<List<Quiz>>
    abstract suspend fun fetchQuiz()
}