package ygmd.kmpquiz.ui.screen.qandas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.domain.viewModel.error.UiEvent
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.saved.PersistanceIntent
import ygmd.kmpquiz.domain.viewModel.qandas.saved.QandaFilterIntent
import ygmd.kmpquiz.domain.viewModel.qandas.saved.SavedQandasViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.qanda.SavedQandaCard
import ygmd.kmpquiz.ui.theme.Dimens.PaddingSmall


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCategoryScreen(
    categoryId: String,
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateToQandaCreation: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    savedQandasViewModel: SavedQandasViewModel = koinViewModel(),
    editViewModel: QandaEditViewModel = koinViewModel(parameters = { parametersOf(null) })
) {
    val qandasUiState by savedQandasViewModel.savedState.collectAsState(UiState.Loading)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(categoryId){
        savedQandasViewModel.processFilterIntent(QandaFilterIntent.CategoryFilter(categoryId))
    }

    LaunchedEffect(Unit) {
        editViewModel.qandaEditEvents.collectLatest {
            when (it) {
                is UiEvent.Error -> snackbarHostState.showSnackbar(it.error.message)
                is UiEvent.Success -> snackbarHostState.showSnackbar(it.message)
            }
        }
    }

    Scaffold(
        topBar = {
            val categoryName = (qandasUiState as? UiState.Success)
                ?.data?.firstOrNull()?.category?.name ?: categoryId

            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Category: $categoryName",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToQandaCreation(categoryId) }) {
                        Icon(Icons.Outlined.Add, contentDescription = "Add")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = qandasUiState) {
                is UiState.Loading -> LoadingState(modifier = Modifier.fillMaxSize())
                is UiState.Error -> ErrorState(
                    modifier = Modifier.fillMaxSize(),
                    message = state.error.message,
                )

                is UiState.Success -> {
                    val data = state.data
                    if (data.isEmpty()) {
                        EmptyState()
                    } else {
                        QandaList(
                            data = data,
                            onDeleteQanda = { savedQandasViewModel.processPersistenceIntent(PersistanceIntent.DeleteQanda(it)) },
                            onEditQanda = onNavigateToEdit
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QandaList(
    data: List<DisplayableQanda>,
    onDeleteQanda: (String) -> Unit,
    onEditQanda: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(
            items = data,
            key = { it.id }
        ) {
            SavedQandaCard(
                modifier = Modifier.fillParentMaxWidth().padding(PaddingSmall),
                qanda = it,
                onDelete = { onDeleteQanda(it.id) },
                onEdit = { onEditQanda(it.id) }
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No questions found in this category")
    }
}