package ygmd.kmpquiz.domain.viewModel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.qandas.saved.PersistanceIntent
import ygmd.kmpquiz.domain.viewModel.state.UiState

private val logger = Logger.withTag("CategoryViewModel")
class CategoryViewModel(
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    val categories: StateFlow<UiState<List<DisplayableCategory>>> =
        categoryUseCase.observeCategories()
            .map { categories -> categories.map { DisplayableCategory(it.id, it.name) } }
            .map { UiState.Success(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    fun processIntent(persistanceIntent: PersistanceIntent){
        when(persistanceIntent){
            is PersistanceIntent.DeleteCategory -> {
                deleteCategory(persistanceIntent.categoryId)
            }

            else -> logger.e { "Intent not handled: $persistanceIntent" }
        }
    }

    private fun deleteCategory(categoryId: String) {
        logger.i { "Deleting category with id: $categoryId" }
        viewModelScope.launch {
            categoryUseCase.delete(categoryId)
        }
    }
}