package ygmd.kmpquiz.ui.screen.qandas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.domain.viewModel.qandas.saved.PersistanceIntent
import ygmd.kmpquiz.domain.viewModel.qandas.saved.SavedQandasViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.qanda.SavedCategoryCard
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCategoriesScreen(
    viewmodel: SavedQandasViewModel = koinViewModel(),
    onNavigateToSavedByCategory: (categoryId: String) -> Unit = {},
) {
    val savedState = viewmodel.savedState.collectAsState(UiState.Loading)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved Categories", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val state = savedState.value) {
                is UiState.Loading -> LoadingState()
                is UiState.Error<*> -> ErrorState(state.error.message)
                is UiState.Success<List<DisplayableQanda>> -> {
                    Box {
                        if (state.data.isEmpty()) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "No saved categories"
                            )
                        } else {
                            val qandasByCategory = state.data.groupBy { it.category }
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(DefaultPadding),
                                verticalArrangement = Arrangement.spacedBy(DefaultPadding),
                            ) {
                                items(
                                    items = qandasByCategory.keys.toList(),
                                    key = { it.id }
                                ) { entry ->
                                    SavedCategoryCard(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onNavigateToSavedByCategory(entry.id)
                                            },
                                        category = entry.name,
                                        questionsSize = qandasByCategory[entry]?.size ?: 0,
                                        onDeleteCategory = {
                                            viewmodel.processPersistenceIntent(
                                                PersistanceIntent.DeleteCategory(
                                                    entry.id
                                                )
                                            )
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String?) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}
