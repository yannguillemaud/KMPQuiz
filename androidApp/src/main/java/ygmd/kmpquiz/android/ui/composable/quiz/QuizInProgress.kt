package ygmd.kmpquiz.android.ui.composable.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.entities.qanda.AnswersFactory
import ygmd.kmpquiz.domain.entities.qanda.Choice
import ygmd.kmpquiz.domain.entities.qanda.Metadata
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.entities.quiz.QuizSession
import ygmd.kmpquiz.viewModel.quiz.session.QuizSessionUiState

@Composable
fun QuizInProgressSection(
    quiz: QuizSessionUiState.InProgress,
    onAnswerSelected: (Choice) -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val session = quiz.session
    val qanda = quiz.currentQanda
    val choices = quiz.shuffledAnswers.choices

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxSize()) {
            QuizHeader(
                currentQuestion = session.currentIndex + 1,
                totalQuestions = session.qandas.size,
                category = qanda.metadata.category,
                onNavigateBack = onNavigateBack
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    QuestionCard(question = qanda.question)
                }

                items(
                    items = choices,
                    key = { it.contextKey }
                ) { choice ->
                    ChoiceCard(
                        choice = choice,
                        isSelected = choice == quiz.selectedAnswer,
                        isAnswered = quiz.hasAnswered,
                        isCorrect = if (quiz.hasAnswered) choice == qanda.correctAnswer else null,
                        onClick = {
                            onAnswerSelected(choice)
                        }
                    )
                }
            }
        }

        if (quiz.hasAnswered) {
            NextQuestionButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp),
                isQuizComplete = session.isComplete,
                onClick = onNextQuestion,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizInProgressSectionPreview() {
    val textQuestion = Question.TextQuestion("Quelle est la capitale de la France ?")
    val imageQuestion = Question.ImageQuestion(
        imageUrl = "https://raw.githubusercontent.com/yannguillemaud/cs2-map-positions/main/inferno/logs.png"
    )

    val session = QuizSession(
        quizId = "1",
        title = "Test Quiz",
        qandas = listOf(
            ygmd.kmpquiz.domain.entities.qanda.Qanda(
                id = "1",
                question = imageQuestion,
                answers = AnswersFactory.createMultipleTextChoices(
                    "Paris",
                    listOf("Marseille", "Lyon", "Toulouse")
                ),
                metadata = Metadata(
                    category = "Géographie",
                    difficulty = "Facile"
                )
            )
        )
    )
    QuizInProgressSection(
        quiz = QuizSessionUiState.InProgress(
            session = session,
            shuffledAnswers = session.currentQanda?.answers?.shuffled()!!
        ),
        onAnswerSelected = {},
        onNextQuestion = {},
        onNavigateBack = {}
    )
}