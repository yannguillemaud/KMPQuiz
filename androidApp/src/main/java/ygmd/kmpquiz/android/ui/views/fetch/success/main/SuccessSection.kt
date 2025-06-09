package ygmd.kmpquiz.android.ui.views.fetch.success.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.android.ui.composable.CategoriesSection
import ygmd.kmpquiz.domain.pojo.contentKey
import ygmd.kmpquiz.viewModel.QandaUiState
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchState
import ygmd.kmpquiz.viewModel.save.SavedQandasUiState
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel

@Composable
fun SuccessSection(
    state: FetchState.Success,
    fetchQandasViewModel: FetchQandasViewModel,
    saveViewModel: SavedQandasViewModel,
) {
    var selectedCategories by remember { mutableStateOf(emptySet<String>()) }

    val savedState by saveViewModel.savedState.collectAsState()

    val filteredQandas by remember {
        derivedStateOf {
            state.availableQandas
                .filter { qanda ->
                    selectedCategories.isEmpty() ||
                            selectedCategories.any { qanda.containsQuery(it) }
                }
                .filter {
                    when(val saved = savedState){
                        is SavedQandasUiState.Success -> {
                            !saved.containsContentKey(it.qanda.contentKey())
                        } else -> true
                    }
                }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Headers avec catégories
            CategoriesSection(
                categories = filteredQandas.map { it.qanda.category },
                onCategorySelected = {
                    if (!selectedCategories.contains(it))
                        selectedCategories += it
                    else selectedCategories -= it
                }
            )

            // Section principale avec les quiz
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Liste des quiz disponibles
                items(
                    items = filteredQandas,
                    key = { it.qanda.contentKey() }
                ) {
                    QandaFetchCard(
                        qandaState = it,
                        onSaveClick = {
                            saveViewModel.saveQanda(it.qanda)
                        }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth()
            )

            if(filteredQandas.isEmpty())
                EmptySection(onRetry = { fetchQandasViewModel.fetchQandas() })
            else FetchStatCard(
                quizCount = filteredQandas.size,
                categories = selectedCategories.size,
                onSaveAll = {
                    saveViewModel.saveAll(
                        filteredQandas.map { it.qanda }
                    )
                }
            )
        }
    }
}

private fun QandaUiState.containsQuery(searchQuery: String): Boolean =
    if (searchQuery.isBlank()) true else {
        val containsInQuestion = qanda.question.contains(searchQuery, ignoreCase = true)
        val containsInCategory = qanda.category.contains(searchQuery, ignoreCase = true)
        val containsInAnswer = qanda.answers.any { it.contains(searchQuery, ignoreCase = true) }
        containsInQuestion || containsInCategory || containsInAnswer
    }