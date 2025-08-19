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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchIntentAction
import ygmd.kmpquiz.viewModel.fetch.FetchQandasViewModel
import ygmd.kmpquiz.viewModel.fetch.FetchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FetchScreen(
    onNavigateBack: () -> Unit = {},
    fetchViewModel: FetchQandasViewModel = koinViewModel(),
) {
    val fetchState by fetchViewModel.fetchState.collectAsState(FetchUiState())
    val canExecuteFetch = fetchViewModel.canExecuteFetch.collectAsState(true)

    Scaffold(
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
            when {
                fetchState.error != null -> Text(fetchState.error!!.errorMessage)
                else -> {
                    val qandasByCategory = fetchState.qandasByCategory
                    val categories = qandasByCategory.keys
                    Button(
                        onClick = { fetchViewModel.onIntentAction(FetchIntentAction.OnRefresh) },
                        enabled = canExecuteFetch.value
                    ) {
                        Text("Refresh")
                    }
                    LazyColumn {
                        items(categories.toList()) {
                            val groupedQandasState = qandasByCategory[it]!!
                            GroupedDraftQandasCard(
                                identifier = it,
                                qandas = groupedQandasState.qandas,
                                onSaveAction = {
                                    fetchViewModel.onIntentAction(
                                        FetchIntentAction.OnSave(groupedQandasState)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}