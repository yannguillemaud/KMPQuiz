package ygmd.kmpquiz.android.ui.views.quiz

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun QuizScreen(
//    onNavigateBack: () -> Unit,
//    onCreateQuiz: () -> Unit,
//    onStartQuiz: (String) -> Unit,
//    viewModel: QuizViewModel = koinViewModel()
//) {
//    val state by viewModel.quizzesState.collectAsState(QuizzesUiState())
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Mes Quiz") },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
//                    }
//                }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = onCreateQuiz
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Ajouter quiz")
//            }
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(state.quizzes.values.toList()) { quiz ->
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    shape = RoundedCornerShape(12.dp),
//                    elevation = CardDefaults.cardElevation(4.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .padding(16.dp)
//                            .fillMaxWidth()
//                            .clickable(
//                                enabled = quiz.qandas.isNotEmpty(),
//                                onClick = { onStartQuiz(quiz.id) }
//                            ),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Column {
//                            Text(quiz.title, style = MaterialTheme.typography.titleMedium)
//                            Text("${quiz.qandas.size} questions", style = MaterialTheme.typography.bodyMedium)
//                        }
//                        Button(
//                            onClick = {
//                                viewModel.processIntent(QuizIntent.DeleteQuiz(quiz.id))
//                            }
//                        ) {
//                            Text("Supprimer")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}