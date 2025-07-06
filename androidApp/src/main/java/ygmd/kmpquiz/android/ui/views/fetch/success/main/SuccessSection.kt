package ygmd.kmpquiz.android.ui.views.fetch.success.main

import androidx.compose.runtime.Composable
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel

@Composable
fun SuccessSection(
    fetchQandasViewModel: FetchQandasViewModel,
) {
//    val fetchedQandas = fetchQandasViewModel.fetchState.collectAsState()
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Section principale avec les quiz
//            when {
//                fetchedQandas.value is FetchState.Success -> {
//                    LazyColumn(
//                        modifier = Modifier
//                            .weight(1f)
//                            .fillMaxWidth(),
//                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        // Liste des quiz disponibles
//                        items(
//                            items = fetchedQandas.,
//                            key = { it }
//                        ) {
//                            QandaFetchCard(
//                                qandaState = fetchedQandas.value,
//                                onSaveClick = {
//                                    saveViewModel.saveQanda(it.qanda)
//                                }
//                            )
//                        }
//                    }
//
//
//                    HorizontalDivider(
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    if (filteredQandas.isEmpty())
//                        EmptySection(onRetry = { fetchQandasViewModel.fetchQandas() })
//                    else FetchStatCard(
//                        quizCount = filteredQandas.size,
//                        categories = selectedCategories.size,
//                        onSaveAll = {
//                            saveViewModel.saveAll(
//                                filteredQandas.map { it.qanda }
//                            )
//                        }
//                    )
//                }
//            }
//        }
//    }
}