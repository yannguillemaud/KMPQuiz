package ygmd.kmpquiz.ui.screen.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import quizSettingsFormItems
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.events.event.Event

/**
 * Screen responsible for creating a new quiz
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param viewmodel ViewModel responsible for managing the state of the screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCreationScreen(
    onNavigateBack: () -> Unit = {},
    viewmodel: QuizEditViewModel = koinViewModel(),
) {
    val quizEditUiState by viewmodel.quizState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewmodel.events) {
        viewmodel.events.collect { event ->
            when (event) {
                is Event.NavBackEvent -> onNavigateBack()
                is Event.SnackbarEvent -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Quiz", fontWeight = FontWeight.Black, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // 2. FAB avec feedback visuel de chargement
            ExtendedFloatingActionButton(
                onClick = {
                    if (!quizEditUiState.metadata.isSaving && !quizEditUiState.metadata.isLoading)
                        viewmodel.saveQuiz()
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                icon = {
                    if (quizEditUiState.metadata.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                    }
                },
                text = {
                    Text(if (quizEditUiState.metadata.isLoading) "Creating..." else "Create Quiz")
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (quizEditUiState.metadata.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 88.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                quizSettingsFormItems(
                    quiz = quizEditUiState,
                    onTitleChange = viewmodel::updateTitle,
                    onCategoryToggled = viewmodel::toggleSelectCategory,
                    onCronChange = viewmodel::toggleSelectCron,
                    onSwitchConfig = viewmodel::switchConfigMode,
                    onGlobalLimitChanged = viewmodel::updateGlobalLimit,
                    onCategoryLimitChanged = viewmodel::updateCategoryLimit
                )
            }
        }
    }
}