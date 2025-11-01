package ygmd.kmpquiz.ui.screen.fetch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.fetch.FetchIntent
import ygmd.kmpquiz.domain.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.fetch.FetcherCard
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchScreen(
    viewModel: FetchQandasViewModel = koinViewModel()
) {
    val uiState by viewModel.fetchersUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is UiEvent.Error -> snackbarHostState.showSnackbar(message = event.error.message)
                is UiEvent.Success -> snackbarHostState.showSnackbar(message = event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Available fetchers", fontWeight = FontWeight.Bold, fontSize = 22.sp)},
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when(val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator()
                }
                is UiState.Error -> {
                    ErrorState(message = state.error.message)
                }
                is UiState.Success if(state.data.isEmpty()) -> {
                    EmptyState()
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(DefaultPadding),
                        verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                    ) {
                        items(
                            items = state.data,
                            key = { it.id }
                        ) { fetcher ->
                            FetcherCard(
                                modifier = Modifier.fillMaxWidth(),
                                name = fetcher.name,
                                isUptodate = false,
                                isLoading = fetcher.isLoading,
                                onFetchInvoke = { viewModel.processIntent(FetchIntent.Fetch(fetcher.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "Aucune source de données disponible",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Vérifiez votre configuration ou contactez le support.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
