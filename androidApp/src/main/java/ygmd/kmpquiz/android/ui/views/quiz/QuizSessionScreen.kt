package ygmd.kmpquiz.android.ui.views.quiz

//@Composable
//fun QuizSessionScreen(
//    quizId: String,
//    onNavigateHome: () -> Unit,
//    onSessionFinished: () -> Unit,
//    viewModel: QuizSessionViewModel = koinViewModel()
//) {
//    val uiState by viewModel.quizUiState.collectAsState()
//
//    LaunchedEffect(quizId) {
//        viewModel.processIntent(QuizSessionIntent.StartQuizSession(quizId))
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8F9FA))
//            .statusBarsPadding()
//    ) {
//        when (val state = uiState) {
//            is QuizSessionUiState.Idle -> QuizLoadingSection()
//
//            is QuizSessionUiState.InProgress -> QuizInProgressSection(
//                quiz = state,
//                onAnswerSelected = { viewModel.processIntent(QuizSessionIntent.SelectAnswer(it)) },
//                onNextQuestion = { viewModel.processIntent(QuizSessionIntent.NextQuestion) },
//                onNavigateBack = onSessionFinished,
//            )
//
//            is QuizSessionUiState.Completed -> QuizCompletedSection(
//                state = state,
//                onNavigateBack = onNavigateHome
//            )
//
//            is QuizSessionUiState.Error -> QuizErrorSection(
//                message = state.message,
//                onNavigateBack = onNavigateHome
//            )
//        }
//    }
//}
