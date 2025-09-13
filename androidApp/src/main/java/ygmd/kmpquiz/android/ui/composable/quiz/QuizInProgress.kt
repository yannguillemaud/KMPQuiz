package ygmd.kmpquiz.android.ui.composable.quiz

//@Composable
//fun QuizInProgressSection(
//    quiz: QuizSessionUiState.InProgress,
//    onAnswerSelected: (Choice) -> Unit,
//    onNextQuestion: () -> Unit,
//    onNavigateBack: () -> Unit,
//) {
//    val session = quiz.session
//    val qanda = quiz.currentQanda
//    val choices = quiz.shuffledAnswers.choices
//
//    Box(modifier = Modifier.fillMaxWidth()) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            QuizHeader(
//                currentQuestion = session.currentIndex + 1,
//                totalQuestions = session.qandas.size,
//                category = qanda.metadata.category,
//                onNavigateBack = onNavigateBack
//            )
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxSize(),
//                contentPadding = PaddingValues(24.dp),
//                verticalArrangement = Arrangement.spacedBy(20.dp)
//            ) {
//                item {
//                    QuestionCard(question = qanda.question)
//                }
//
//                items(
//                    items = choices,
//                    key = { it.contextKey }
//                ) { choice ->
//                    ChoiceCard(
//                        choice = choice,
//                        isSelected = choice == quiz.selectedAnswer,
//                        isAnswered = quiz.hasAnswered,
//                        isCorrect = if (quiz.hasAnswered) choice == qanda.correctAnswer else null,
//                        onClick = {
//                            onAnswerSelected(choice)
//                        }
//                    )
//                }
//            }
//        }
//
//        if (quiz.hasAnswered) {
//            NextQuestionButton(
//                modifier = Modifier
//                    .align(Alignment.BottomCenter)
//                    .navigationBarsPadding()
//                    .padding(bottom = 16.dp),
//                isQuizComplete = session.isComplete,
//                onClick = onNextQuestion,
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun QuizInProgressSectionPreview() {
//    val textQuestion = Question.TextQuestion("Quelle est la capitale de la France ?")
//    val imageQuestion = Question.ImageQuestion(
//        imageUrl = "https://raw.githubusercontent.com/yannguillemaud/cs2-map-positions/main/inferno/logs.png"
//    )
//
//    val session = QuizSession(
//        quizId = "1",
//        title = "Test Quiz",
//        qandas = listOf(
//            ygmd.kmpquiz.domain.entities.qanda.Qanda(
//                id = "1",
//                question = imageQuestion,
//                answers = AnswersFactory.createMultipleTextChoices(
//                    "Paris",
//                    listOf("Marseille", "Lyon", "Toulouse")
//                ),
//                metadata = Metadata(
//                    category = "Géographie",
//                    difficulty = "Facile"
//                )
//            )
//        )
//    )
//    QuizInProgressSection(
//        quiz = QuizSessionUiState.InProgress(
//            session = session,
//            shuffledAnswers = session.currentQanda?.answers?.shuffled()!!
//        ),
//        onAnswerSelected = {},
//        onNextQuestion = {},
//        onNavigateBack = {}
//    )
//}