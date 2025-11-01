package ygmd.kmpquiz.ui.screen.qandas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ygmd.kmpquiz.domain.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditIntent
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditUiState
import ygmd.kmpquiz.domain.viewModel.qandas.edit.QandaEditViewModel
import ygmd.kmpquiz.domain.viewModel.state.UiState
import ygmd.kmpquiz.domain.viewModel.state.getOrDefault
import ygmd.kmpquiz.ui.composable.createquiz.LoadingState
import ygmd.kmpquiz.ui.composable.playquiz.ErrorState
import ygmd.kmpquiz.ui.composable.qanda.QandaEditForm
import ygmd.kmpquiz.ui.theme.Dimens.DefaultPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QandaEditScreen(
    qandaId: String,
    onNavigateBack: () -> Unit = {},
    qandaEditViewModel: QandaEditViewModel = koinViewModel(parameters = { parametersOf(qandaId) }),
    categoryViewModel: CategoryViewModel = koinViewModel(),
) {
    val editUiState: UiState<QandaEditUiState> by qandaEditViewModel.qandaEditState.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Qanda settings", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    qandaEditViewModel.processIntent(QandaEditIntent.Save)
                    onNavigateBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Save,
                    contentDescription = "Save"
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(DefaultPadding)) {
            when (val state = editUiState) {
                is UiState.Loading -> LoadingState()
                is UiState.Error -> ErrorState(message = state.error.message)
                is UiState.Success -> {
                    QandaEditForm(
                        modifier = Modifier.fillMaxSize(),
                        question = state.data.question,
                        availableCategories = categories.getOrDefault(emptyList()),
                        onCategorySelected = {
                            qandaEditViewModel.processIntent(QandaEditIntent.UpdateCategory(it.id))
                        },
                        selectedCategory = state.data.category,
                        correctAnswer = state.data.correctAnswer,
                        incorrectAnswers = state.data.incorrectAnswers,
                        canAddIncorrectAnswer = state.data.canAddIncorrectAnswer,
                        onUpdateQuestion = {
                            qandaEditViewModel.processIntent(QandaEditIntent.UpdateQuestion(it))
                        },
                        onAddNewIncorrectAnswer = {
                            qandaEditViewModel.processIntent(QandaEditIntent.AddNewIncorrectAnswer)
                        },
                        onRemoveIncorrectAnswer = {
                            qandaEditViewModel.processIntent(QandaEditIntent.RemoveAnswer(it))
                        },
                        onUpdateIncorrectAnswer = { idx, entry ->
                            qandaEditViewModel.processIntent(
                                QandaEditIntent.UpdateIncorrectAnswers(
                                    idx,
                                    entry
                                )
                            )
                        },
                        onUpdateCorrectAnswer = {
                            qandaEditViewModel.processIntent(QandaEditIntent.UpdateCorrectAnswer(it))
                        },
                    )
                }
            }
        }
    }
}