package ygmd.kmpquiz.android.ui.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.QandaComposable
import ygmd.kmpquiz.viewModel.FetchQandasVModel
import ygmd.kmpquiz.viewModel.SaveQandasVModel

@Composable
fun DisplaySavedQandas(viewModel: SaveQandasVModel = koinViewModel()){
    val state by viewModel.savedState.collectAsState()

    when {
        state.isLoading -> {
            CenteredCircularProgressIndicator()
        }
        state.qandas.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("NO QANDA SAVED")
            }
        }
        else -> {
            LazyColumn {
                items(state.qandas){
                    QandaComposable(it)
                }
            }
        }
    }
}