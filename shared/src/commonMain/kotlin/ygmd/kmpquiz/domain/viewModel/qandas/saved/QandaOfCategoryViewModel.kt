package ygmd.kmpquiz.domain.viewModel.qandas.saved

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda

private val logger = Logger.withTag("SavedQandasViewModel")

sealed interface CategoryQuestionsState {
    data object Loading : CategoryQuestionsState
    data class Success(
        val category: DisplayableCategory,
        val qandas: List<DisplayableQanda> = emptyList()
    ) : CategoryQuestionsState
    data class Error(val message: String) : CategoryQuestionsState
}

class CategoryQandaViewModel(
    savedStateHandle: SavedStateHandle,
    private val categoryUseCase: CategoryUseCase,
    private val getQandasUseCase: GetQandaUseCase
): ViewModel(){
    private val categoryId: String = checkNotNull(savedStateHandle["categoryId"])
    private val _uiState = MutableStateFlow<CategoryQuestionsState>(CategoryQuestionsState.Loading)
    val qandasUiState: StateFlow<CategoryQuestionsState> = _uiState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val category = categoryUseCase.getById(categoryId)
                if(category == null){
                    _uiState.update { CategoryQuestionsState.Error("Category not found") }
                    return@launch
                }
                val questionsDeferred = async { getQandasUseCase.getByCategory(categoryId) }
                val fetchedQuestions = questionsDeferred.await().map {
                    DisplayableQanda(
                        id = it.id,
                        contextKey = it.contextKey,
                        category = DisplayableCategory(category.id, category.name),
                        question = it.question,
                        answers = it.answers
                    )
                }
                _uiState.update {
                    CategoryQuestionsState.Success(
                        category = DisplayableCategory(category.id, category.name),
                        qandas = fetchedQuestions
                    )
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to load category questions" }
                _uiState.update { CategoryQuestionsState.Error("Failed to load category questions") }
            }
        }
    }
}