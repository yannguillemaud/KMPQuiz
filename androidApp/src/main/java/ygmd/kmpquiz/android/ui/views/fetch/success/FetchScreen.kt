package ygmd.kmpquiz.android.ui.views.fetch.success

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.merge
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.viewModel.coordinator.FetchScreenCoordinator
import ygmd.kmpquiz.viewModel.coordinator.FetchScreenState
import ygmd.kmpquiz.viewModel.error.UiEvent
import ygmd.kmpquiz.viewModel.fetch.FetchIntentAction
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.save.PersistanceIntent
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchScreen(
    onNavigateBack: () -> Unit = {},
    coordinator: FetchScreenCoordinator = koinViewModel(),
    fetchQandasViewModel: FetchQandasViewModel = koinViewModel(),
    savedQandasViewModel: SavedQandasViewModel = koinViewModel(),
) {
    val state by coordinator.state.collectAsState(FetchScreenState())
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        merge(
            fetchQandasViewModel.events,
            savedQandasViewModel.events
        ).collect { event ->
            val result = when (event) {
                is UiEvent.Success -> {}
                is UiEvent.Error -> snackbarHostState.showSnackbar(
                    event.message,
                    actionLabel = event.action?.label
                )
            }
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch)
                }

                SnackbarResult.Dismissed -> {}
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Refresh")
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Fetch",
                        style = typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            val qandasByCategory = state.qandasByCategory
            val categories = qandasByCategory.keys
            Button(
                onClick = { fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch) },
                enabled = state.canRefresh
            ) {
                Text("Refresh")
            }
            LazyColumn {
                items(categories.toList()) {
                    val groupedQandasState = qandasByCategory[it]!!
                    GroupedDraftQandasCard(
                        identifier = it,
                        qandas = groupedQandasState,
                        onSaveAction = {
                            savedQandasViewModel.processIntent(
                                PersistanceIntent.SaveAll(groupedQandasState)
                            )
                        }
                    )
                }
            }
        }
    }
}