import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import ygmd.kmpquiz.domain.Quiz
import ygmd.kmpquiz.domain.QuizUseCase
import ygmd.kmpquiz.service.models.QuizViewModel

class QuizViewModelImpl(private val quizUseCase: QuizUseCase): QuizViewModel() {
    private val _quizzesFlow = MutableStateFlow(quizUseCase.getAvailableQuizzes())

    override val quizzesFlow: StateFlow<List<Quiz>>
        get() = _quizzesFlow

    override suspend fun fetchQuiz(){
        _quizzesFlow.update { quizUseCase.fetchQuizzes() }
    }
}