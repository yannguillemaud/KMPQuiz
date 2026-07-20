package ygmd.kmpquiz.presentation.screen.quiz

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.composable.playquiz.QuestionReviewCard
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.quiz.session.CategoryReviewUiState
import ygmd.kmpquiz.presentation.viewModel.quiz.session.CategoryReviewViewModel

/**
 * Reviews every question answered in a single category of a completed session: question,
 * the user's pick, and the correct answer, rendered through [QuestionReviewCard]. Reached by
 * tapping a category row on the quiz finish screen.
 *
 * Back navigation uses plain `goBack()` (wired in [App]) — this screen sits inside the still-live
 * finish flow, so popping returns to the finish screen with its state intact, unlike
 * `SessionDetails` which clears its tab to root.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryReviewScreen(
    sessionId: String,
    categoryId: String,
    categoryName: String,
    onNavigateBack: () -> Unit = {},
    viewModel: CategoryReviewViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initialize(sessionId, categoryId, categoryName)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText = when (val reviewState = state) {
                        is CategoryReviewUiState.Loaded -> reviewState.categoryName
                        else -> categoryName
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
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Crossfade(
            targetState = state,
            label = "CategoryReviewTransition",
            animationSpec = tween(300),
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) { reviewState ->
            when (reviewState) {
                is CategoryReviewUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is CategoryReviewUiState.Error -> ErrorState(message = reviewState.message)

                is CategoryReviewUiState.Loaded -> {
                    if (reviewState.userAnswers.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.fillMaxSize(),
                            icon = Icons.Outlined.QuestionAnswer,
                            title = "No questions to review",
                            description = "This category has no answered questions in this session.",
                            fullSizeState = true
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = Dimens.PaddingMedium,
                                end = Dimens.PaddingMedium,
                                top = Dimens.PaddingMedium,
                                bottom = Dimens.PaddingLarge,
                            ),
                            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
                        ) {
                            itemsIndexed(
                                items = reviewState.userAnswers,
                                key = { idx, _ -> idx }
                            ) { _, (qanda, userAnswer) ->
                                QuestionReviewCard(qanda = qanda, userAnswer = userAnswer)
                            }
                        }
                    }
                }
            }
        }
    }
}
