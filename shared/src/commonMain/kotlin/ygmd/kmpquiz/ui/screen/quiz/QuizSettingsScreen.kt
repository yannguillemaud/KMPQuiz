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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditViewModel
import ygmd.kmpquiz.events.event.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSettingsScreen(
    quizId: String,
    onNavigateBack: () -> Unit = {},
    viewmodel: QuizEditViewModel = koinViewModel(),
) {
    val state by viewmodel.quizState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(quizId) { viewmodel.setUp(quizId) }

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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Black, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (!state.metadata.isSaving) viewmodel.saveQuiz()
                },
                icon = {
                    if (state.metadata.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Icon(Icons.Default.Save, "Save")
                    }
                },
                text = {
                    Text(if (state.metadata.isSaving) "Saving..." else "Save")
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        if (state.metadata.isLoading) {
            Box(
                Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 80.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                quizSettingsFormItems(
                    quiz = state,
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