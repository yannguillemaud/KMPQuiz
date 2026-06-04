package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QandaUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionIntent
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.QuizSessionViewModel
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSessionScreen(
    isNewSession: Boolean,
    sessionId: String? = null,
    onShowHistory: () -> Unit = {},
    onNavigateToResults: (sessionId: String) -> Unit = {},
    quizSessionViewModel: QuizSessionViewModel = koinViewModel(),
) {
    val quizUiState by quizSessionViewModel.sessionState.collectAsState()

    LaunchedEffect(isNewSession) {
        if (isNewSession) quizSessionViewModel.processIntent(QuizSessionIntent.CreateSession)
        else if (sessionId != null) quizSessionViewModel.processIntent(
            QuizSessionIntent.ResumeSession(sessionId)
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Keep the TopAppBar clean. Only show the quiz title, regardless of the state.
                    val titleText = when (val state = quizUiState) {
                        is QuizSessionUiState.Started -> state.title
                        is QuizSessionUiState.Completed -> state.title
                        is QuizSessionUiState.Idle -> state.title
                        else -> "Quiz"
                    }
                    Text(
                        text = titleText,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = quizUiState,
            label = "QuizStateTransition",
            animationSpec = tween(durationMillis = 300),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { state ->
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (state) {
                    is QuizSessionUiState.Loading -> CircularProgressIndicator()

                    is QuizSessionUiState.Error -> ErrorState(message = state.message)

                    is QuizSessionUiState.Completed -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                text = "Quiz Completed!",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateToResults(state.sessionId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View Results")
                            }
                            OutlinedButton(
                                onClick = onShowHistory,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Back to History")
                            }
                        }
                    }

                    is QuizSessionUiState.Idle -> {
                        Button(onClick = { quizSessionViewModel.processIntent(QuizSessionIntent.StartSession) }) {
                            Text("Start Quiz")
                        }
                    }

                    is QuizSessionUiState.Started -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            QuizProgressIndicator(
                                currentStep = state.index + 1,
                                totalSteps = state.questionsCount,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            QuizSessionSection(
                                modifier = Modifier.weight(1f), // Takes remaining vertical space
                                category = state.currentQanda.category,
                                questionContent = state.currentQanda.question,
                                answers = state.currentQanda.answers,
                                onSubmitAnswer = { answerId ->
                                    quizSessionViewModel.processIntent(
                                        QuizSessionIntent.SubmitAnswer(answerId)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizSessionSection(
    modifier: Modifier = Modifier,
    category: String,
    questionContent: QandaUiState.QuestionUiState,
    answers: List<QandaUiState.AnswerUiState>,
    onSubmitAnswer: (answerId: String) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                when (questionContent) {
                    is QandaUiState.QuestionUiState.ImageQuestionUiState -> {
                        AsyncImage(
                            model = questionContent.imageUrl,
                            contentDescription = "Question Image",
                            contentScale = ContentScale.Fit, // Fit prevents cutting off diagrams/text in images
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }

                    is QandaUiState.QuestionUiState.TextQuestionUiState -> {
                        Text(
                            text = questionContent.text,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // LazyColumn scrolls independently, avoiding layout breakage
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(answers, key = { it.id }) { answer ->
                OutlinedCard(
                    onClick = { onSubmitAnswer(answer.id) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = answer.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun QuizProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val progress = if (totalSteps > 0) currentStep.toFloat() / totalSteps else 0f

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(8.dp)
                .clip(CircleShape),
            strokeCap = StrokeCap.Round,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Question $currentStep of $totalSteps",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}