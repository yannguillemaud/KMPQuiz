package ygmd.kmpquiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import ygmd.kmpquiz.android.ui.QuizList
import ygmd.kmpquiz.service.models.QuizViewModel

class Main: ComponentActivity(){
    private val viewModel: QuizViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                MainActivity(viewModel)
            }
        }
    }
}

@Composable
fun MainActivity(viewModel: QuizViewModel) {
    val flow by viewModel.quizzesFlow.collectAsStateWithLifecycle()
    val scope = viewModel.viewModelScope

    // TODO - set quiz as composable
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(vertical = 40.dp),
            onClick = { scope.launch { viewModel.fetchQuiz() } },
            colors = ButtonColors(Color.Black, Color.Yellow, Color.Black, Color.Black)
        ) { Text("FETCH") }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuizList(flow) {
            Button(onClick = { println(it) }) {
                Text(it.category)
            }
        }
    }
}