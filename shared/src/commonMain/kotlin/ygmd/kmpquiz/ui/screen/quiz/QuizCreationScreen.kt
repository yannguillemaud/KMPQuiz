package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditIntent
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.domain.viewModel.state.getOrDefault
import ygmd.kmpquiz.domain.viewModel.state.map
import ygmd.kmpquiz.ui.composable.createquiz.QuizSettingsForm
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreationScreen(
    onNavigateBack: () -> Unit = {},
    onFinished: () -> Unit = {},
    quizEditViewModel: QuizEditViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel(),
) {
    val quizEditUiState = quizEditViewModel.quizEditUiState.collectAsState()
    val availableCategories = categoryViewModel.categories.collectAsState()
    val availableCrons = quizEditViewModel.availableCrons.collectAsState()

    val title = quizEditUiState.value.map { it.title }.getOrDefault("")
    val titleError = quizEditUiState.value.map { it.titleError }.getOrDefault(null)
    val categories = quizEditUiState.value.map { it.categories }.getOrDefault(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit){
        quizEditViewModel.events.collectLatest {
            if (it is UiEvent.Error) snackbarHostState.showSnackbar(it.error.message)
            else onFinished()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create new quiz", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    quizEditViewModel.processIntent(intent = QuizEditIntent.Save)
                }
            ) { Icon(Icons.Default.Save, contentDescription = "Create Quiz") }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(DefaultPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuizSettingsForm(
                title = title,
                onTitleChange = {
                    quizEditViewModel.processIntent(QuizEditIntent.UpdateTitle(it))
                },
                titleError = titleError,
                selectedCategories = categories,
                availableCategories = availableCategories.value.getOrDefault(emptyList()),
                onCategoryChange = {
                    quizEditViewModel.processIntent(QuizEditIntent.UpdateCategories(it.toList()))
                },
                availableCrons = availableCrons.value,
                onCronChange = { quizEditViewModel.processIntent(QuizEditIntent.UpdateCron(it)) }
            )
        }
    }
}
