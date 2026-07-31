package ygmd.kmpquiz.presentation.screen.quiz.history

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.composable.playquiz.GlobalScoreSection
import ygmd.kmpquiz.presentation.composable.qanda.QandaImagePreview
import ygmd.kmpquiz.presentation.viewModel.quiz.session.CategoryStatUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.DetailedSessionUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.DetailedSessionViewModel
import ygmd.kmpquiz.presentation.viewModel.quiz.session.QandaUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.UserAnswerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedSessionHistoryScreen(
    sessionId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: DetailedSessionViewModel = koinViewModel(),
) {
    val state by viewModel.sessionHistoryState.collectAsStateWithLifecycle()
    var expandedImage by remember { mutableStateOf<Pair<String, String?>?>(null) }

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
                    ResultsDashboard(
                        sessionState,
                        onImageClick = { url, altText -> expandedImage = url to altText }
                    )
                }
            }
        }
    }

    expandedImage?.let { (url, altText) ->
        QandaImagePreview(
            imageUrl = url,
            altText = altText,
            onDismiss = { expandedImage = null }
        )
    }
}

@Composable
private fun ResultsDashboard(
    sessionState: DetailedSessionUiState.Loaded,
    onImageClick: (String, String?) -> Unit = { _, _ -> }
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // Bottom padding reserves space for the floating nav capsule so the last
        // reviewed answer card is not obscured.
        contentPadding = PaddingValues(bottom = Dimens.BottomNavPadding),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingLarge)
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
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
            )
        }

        if (sessionState.userAnswers.isEmpty()) {
            item {
                Text(
                    text = "No recorded answers found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
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
                    modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
                    onImageClick = onImageClick
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
            modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
        ) {
            items(categories, key = { it.categoryName }) { category ->
                OutlinedCard(
                    modifier = Modifier.widthIn(min = 140.dp, max = 200.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(Dimens.PaddingMedium),
                        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
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
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(MaterialTheme.shapes.extraSmall),
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
    modifier: Modifier = Modifier,
    qanda: QandaUiState,
    userAnswerState: UserAnswerState,
    onImageClick: (String, String?) -> Unit = { _, _ -> }
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
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
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
                            .clip(MaterialTheme.shapes.medium)
                            .clickable(true, onClick = { onImageClick(content.imageUrl, content.altText) })
                    )
                }
            }

            // User's Answer Highlight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(containerColor)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(Dimens.PaddingMediumSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isCorrect) "Correct Answer" else "Wrong Answer",
                        tint = iconTint
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
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
                        if (!isCorrect) {
                            Column {
                                Text(
                                    text = "Correct Answer",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
