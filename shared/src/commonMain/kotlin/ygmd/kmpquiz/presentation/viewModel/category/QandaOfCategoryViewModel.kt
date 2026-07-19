package ygmd.kmpquiz.presentation.viewModel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.qanda.Answers
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategory

private val logger = Logger.withTag("SavedQandasViewModel")

sealed interface CategoryQuestionsState {
    data object Loading : CategoryQuestionsState
    data class Success(
        val category: DisplayableCategory,
        val qandas: List<DisplayableQanda> = emptyList()
    ) : CategoryQuestionsState
    data class Error(val message: String) : CategoryQuestionsState
}

data class DisplayableQanda(
    val id: String,
    val contextKey: String,
    val category: DisplayableCategory,
    val question: QuestionContent,
    val answers: Answers,
)

class CategoryQandaViewModel(
    private val categoryId: String,
    private val categoryUseCase: CategoryUseCase,
    private val getQandasUseCase: GetQandaUseCase
) : ViewModel() {
    private val _loadingState = MutableStateFlow<CategoryQuestionsState>(CategoryQuestionsState.Loading)
    private val _rawQandas = MutableStateFlow<List<DisplayableQanda>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val qandasUiState: StateFlow<CategoryQuestionsState> = combine(
        _loadingState,
        _rawQandas,
        _searchQuery
    ) { loadState, rawList, query ->
        when (loadState) {
            is CategoryQuestionsState.Loading -> CategoryQuestionsState.Loading
            is CategoryQuestionsState.Error -> loadState
            is CategoryQuestionsState.Success -> {
                val filtered = if (query.isBlank()) rawList
                               else rawList.filter { it.matchesQuery(query) }
                CategoryQuestionsState.Success(category = loadState.category, qandas = filtered)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryQuestionsState.Loading
    )

    init {
        loadData()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val category = categoryUseCase.getById(categoryId)
                if (category == null) {
                    _loadingState.value = CategoryQuestionsState.Error("Category not found")
                    return@launch
                }
                val displayableCategory = DisplayableCategory(category.id, category.name)
                val fetchedQuestions = getQandasUseCase.getByCategory(categoryId).map {
                    DisplayableQanda(
                        id = it.id,
                        contextKey = it.contextKey,
                        category = displayableCategory,
                        question = it.question,
                        answers = it.answers
                    )
                }
                _rawQandas.value = fetchedQuestions
                _loadingState.value = CategoryQuestionsState.Success(
                    category = displayableCategory,
                    qandas = emptyList()
                )
            } catch (e: Exception) {
                logger.e(e) { "Failed to load category questions" }
                _loadingState.value = CategoryQuestionsState.Error("Failed to load category questions")
            }
        }
    }

    private fun DisplayableQanda.matchesQuery(query: String): Boolean {
        val normalized = query.trim().lowercase()
        return contextKey.lowercase().contains(normalized)
            || (question as? QuestionContent.TextContent)?.text?.lowercase()?.contains(normalized) == true
            || answers.correctAnswer.contextKey.lowercase().contains(normalized)
    }
}
