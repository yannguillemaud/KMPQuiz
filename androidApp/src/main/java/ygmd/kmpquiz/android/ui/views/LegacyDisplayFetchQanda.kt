package ygmd.kmpquiz.android.ui.views

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.event.ClickActions
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.viewModel.fetch.FetchQandasVModel

@Composable
fun LegacyDisplayFetchQanda(
    fetchQandasVModel: FetchQandasVModel = koinViewModel(),
//    saveQandaUseCase: SaveQandaUseCase = koinInject(),
) {
    val scope = rememberCoroutineScope()
    val fetchUiState by fetchQandasVModel.fetchedUiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var errorHandled by remember { mutableStateOf(false) }

    val qandaClickActions = ClickActions<InternalQanda>(
        onLongClick = {
//            scope.launch { saveQandaUseCase.saveQanda(it) }
            println("LongClick")
        },
        onDoubleTap = {
//            scope.launch { saveQandaUseCase.saveQanda(it) }
            println("DoubleTap")
        },
    )

    LaunchedEffect(fetchUiState.error) {
        fetchUiState.error?.let {
            if(!errorHandled){
                errorHandled = true
                snackbar.showSnackbar(
                    message = it.errorMessage,
                    actionLabel = "Retry"
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        when {
            fetchUiState.isLoading -> {
                CenteredCircularProgressIndicator()
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {

/*
                    var rememberCategory by rememberSaveable { mutableStateOf("") }
                    val categories by remember {
                        mutableStateOf(fetchUiState.qandas.map { it.category }.toSet())
                    }

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
*/
/*
                        items(categories.toList()) { selectedCategory ->
                            FilterChip(
                                selected = selectedCategory == rememberCategory,
                                onClick = {
                                    rememberCategory =
                                        if (selectedCategory == rememberCategory) "" else selectedCategory
                                },
                                label = { Text(color = Color.White, text = selectedCategory) },
                                enabled = fetchUiState.qandas.isNotEmpty(),
                            )
                        }
*//*

                    }

                    Spacer(modifier = Modifier.height(16.dp))

//                    QandaList(
//                        qandas = fetchUiState.qandas,
//                        modifier = Modifier.fillMaxWidth(),
//                        filter = { it.category == rememberCategory || rememberCategory.isBlank() },
//                    ) { qanda ->
//                        SelectionableQanda(
//                            qanda = qanda,
//                            clickAction = qandaClickActions,
//                        ) { selection ->
//                            QandaComposable(selection)
//                        }
//                    }
*/
                }

            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.Center).padding(16.dp)
        )
    }
}

@Composable
fun CenteredCircularProgressIndicator(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){ CircularProgressIndicator() }
}

@Composable
fun SelectionableQanda(
    qanda: InternalQanda,
    clickAction: ClickActions<InternalQanda>? = null,
    block: @Composable (q: InternalQanda) -> Unit
){
    Box(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = { clickAction?.onClick?.invoke(qanda) },
                onDoubleTap = { clickAction?.onDoubleTap?.invoke(qanda) },
                onLongPress = { clickAction?.onLongClick?.invoke(qanda) },
            )
        }
    ) {
        block(qanda)
    }
}