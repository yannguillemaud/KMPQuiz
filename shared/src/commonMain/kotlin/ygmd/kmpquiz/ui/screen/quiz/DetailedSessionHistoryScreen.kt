package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.model.quiz.session.SessionStats
import ygmd.kmpquiz.domain.viewModel.quiz.session.CategoryStatUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.DetailedSessionUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.DetailedSessionViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.session.QandaUiState
import ygmd.kmpquiz.domain.viewModel.quiz.session.UserAnswerState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedSessionHistoryScreen(
    sessionId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: DetailedSessionViewModel = koinViewModel(),
) {
    val state by viewModel.sessionState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(sessionId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText = when (val session = state) {
                        is DetailedSessionUiState.Loaded -> "${session.title} Results"
                        else -> "Session History"
                    }
                    Text(
                        text = titleText,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Navigate Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = state,
            label = "DetailedSessionTransition",
            animationSpec = tween(300),
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) { sessionState ->
            when (sessionState) {
                is DetailedSessionUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailedSessionUiState.Error -> ErrorState(message = sessionState.message)
                is DetailedSessionUiState.Loaded -> {
                    ResultsDashboard(sessionState)
                }
            }
        }
    }
}

@Composable
private fun ResultsDashboard(sessionState: DetailedSessionUiState.Loaded) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. SECTION : GLOBAL SCORE
        item {
            GlobalScoreSection(stats = sessionState.stats)
        }

        // 2. SECTION : CATEGORIES BREAKDOWN
        if (sessionState.categoriesBreakdown.isNotEmpty()) {
            item {
                CategoryBreakdownSection(categories = sessionState.categoriesBreakdown)
            }
        }

        // 3. SECTION : DETAILED HISTORY
        item {
            Text(
                text = "Detailed Review",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (sessionState.userAnswers.isEmpty()) {
            item {
                Text(
                    text = "No recorded answers found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(
                items = sessionState.userAnswers,
                key = { (qanda, _) -> qanda.category + qanda.hashCode() }
            ) { (qanda, userAnswerState) ->
                HistoryItemCard(
                    qanda = qanda,
                    userAnswerState = userAnswerState,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun GlobalScoreSection(stats: SessionStats) {
    // Animation fluide pour le remplissage du Donut Chart
    val animatedProgress by animateFloatAsState(
        targetValue = stats.globalSuccessRate,
        animationSpec = tween(durationMillis = 1000),
        label = "ScoreAnimation"
    )

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Cercle de fond (Track)
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(160.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )
            // Cercle de score animé
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(160.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 12.dp,
                strokeCap = StrokeCap.Round
            )

            // Texte au centre
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(stats.globalSuccessRate * 100).roundToInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${stats.totalCorrect} / ${stats.totalQuestions} correct",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownSection(categories: List<CategoryStatUiState>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Performance by Category",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // LazyRow permet de scroller horizontalement si on a beaucoup de catégories
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories, key = { it.categoryName }) { category ->
                OutlinedCard(
                    modifier = Modifier.width(160.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category.categoryName.uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "${category.correctAnswers} / ${category.totalQuestions}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        LinearProgressIndicator(
                            progress = { category.successRate },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.secondaryContainer,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    qanda: QandaUiState,
    userAnswerState: UserAnswerState,
    modifier: Modifier = Modifier
) {
    val isCorrect = userAnswerState.isCorrect
    val containerColor = if (isCorrect) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
    }
    val borderColor = if (isCorrect) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.error
    }
    val iconTint = if (isCorrect) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.error
    }
    val icon = if (isCorrect) Icons.Outlined.CheckCircleOutline else Icons.Outlined.Cancel

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Category Badge
            Text(
                text = qanda.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // Question Content
            when (val content = qanda.question) {
                is QandaUiState.QuestionUiState.TextQuestionUiState -> {
                    Text(
                        text = content.text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                is QandaUiState.QuestionUiState.ImageQuestionUiState -> {
                    AsyncImage(
                        model = content.imageUrl,
                        contentDescription = "Question Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // User's Answer Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(containerColor)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isCorrect) "Correct Answer" else "Wrong Answer",
                        tint = iconTint
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Your Answer",
                                style = MaterialTheme.typography.labelMedium,
                                color = iconTint
                            )
                            Text(
                                text = userAnswerState.userAnswerContent.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        if(!isCorrect){
                            Column {
                                Text(
                                    text = "Correct Answer",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = iconTint
                                )
                                Text(
                                    text = userAnswerState.correctAnswerContent.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
