package ygmd.kmpquiz.android.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import ygmd.kmpquiz.domain.Quiz

@Composable
fun QuizList(list: List<Quiz>, block: @Composable (quiz: Quiz) -> Unit){
    LazyColumn {
        list.forEach { item { block(it) } }
    }
}