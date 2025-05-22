package ygmd.kmpquiz.android.ui.model

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.QandaComposable
import ygmd.kmpquiz.android.ui.composable.QandaList
import ygmd.kmpquiz.android.ui.event.ClickActions
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.viewModel.FetchQandasVModel
import ygmd.kmpquiz.viewModel.SaveQandasVModel

@Composable
fun DisplayFetchQanda(
    fetchQandasVModel: FetchQandasVModel = koinViewModel(),
    saveQandasVModel: SaveQandasVModel = koinViewModel()
) {
    val fetchUiState by fetchQandasVModel.fetchedUiState.collectAsState()

    val qandaClickActions = ClickActions<QANDA>(
        onLongClick = { saveQandasVModel.saveQandas(it).also { println("Saving Qanda") } },
        onDoubleTap = { saveQandasVModel.saveQandas(it).also { println("Double tapped") } },
    )

    LaunchedEffect(fetchUiState.qandas.isNotEmpty()) {
        fetchQandasVModel.fetchQandas()
    }

    when {
        fetchUiState.isLoading -> {
            CenteredCircularProgressIndicator()
        }
        fetchUiState.error != null -> {
            Text("ERROR:: ${fetchUiState.error}")
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {

                var rememberCategory by rememberSaveable { mutableStateOf("") }
                val categories = fetchUiState.qandas.map { it.category }.toSet()

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories.toList()) { selectedCategory ->
                        FilterChip(
                            selected = selectedCategory == rememberCategory,
                            onClick = { rememberCategory = if(selectedCategory == rememberCategory) "" else selectedCategory },
                            label = { Text(color = Color.White, text = selectedCategory) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                QandaList(
                    qandas = fetchUiState.qandas,
                    modifier = Modifier.fillMaxWidth(),
                    filter = { it.category == rememberCategory || rememberCategory.isBlank() },
                ) { qanda ->
                    SelectionableQanda(
                        qanda = qanda,
                        clickAction = qandaClickActions,
                    ) { selection ->
                        QandaComposable(selection)
                    }
                }
            }

        }
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
    qanda: QANDA,
    clickAction: ClickActions<QANDA>? = null,
    block: @Composable (q: QANDA) -> Unit
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