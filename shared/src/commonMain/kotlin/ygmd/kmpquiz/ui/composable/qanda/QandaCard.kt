package ygmd.kmpquiz.ui.composable.qanda

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import ygmd.kmpquiz.domain.model.qanda.Question
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.ui.theme.Dimens
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@Composable
fun SavedQandaCard(
    modifier: Modifier = Modifier,
    qanda: DisplayableQanda,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
) {
    var isExpended by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.animateContentSize(),
        elevation = CardDefaults.cardElevation(Dimens.CardElevation),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { isExpended = !isExpended }
                .padding(DefaultPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.fillMaxHeight()) {
                    QuestionView(question = qanda.question)
                }
            }
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete")
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if(isExpended) Icons.Outlined.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand"
                    )
                }
            }
            AnimatedVisibility(visible = isExpended){
                Column(modifier = Modifier.fillMaxSize().animateContentSize()) {
                    val correctAnswer = remember { qanda.answers.correctAnswer }
                    qanda.answers.choices.forEach { answer ->
                        Card {
                            Text(
                                answer.contextKey,
                                color = if(correctAnswer == answer) Color.Green else Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun QuestionView(
    question: Question
) {
    when (question) {
        is Question.TextQuestion -> {
            Text(
                text = question.text,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        is Question.ImageQuestion -> {
            ImageQuestionView(question.imageUrl)
        }
    }
}

@Composable
fun ImageQuestionView(
    imageUrl: String,
) {
    val painter = rememberAsyncImagePainter(imageUrl)
    val state by painter.state.collectAsState()

    when (state) {
        AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> CircularProgressIndicator()

        is AsyncImagePainter.State.Error -> Text(text = "Error")
        is AsyncImagePainter.State.Success ->
            Image(painter, contentDescription = imageUrl)
    }
}

