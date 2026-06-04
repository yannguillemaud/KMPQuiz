package ygmd.kmpquiz.domain.viewModel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategoryWithCount

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<DisplayableCategoryWithCount> = emptyList()
)

class CategoryViewModel(
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    val categories = categoryUseCase.observeCategories()
        .map { allCategories ->
            val categories = allCategories.map {
                DisplayableCategoryWithCount(
                    it.id,
                    it.name,
                    it.questionsCount
                )
            }
            CategoryUiState(categories = categories)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState(isLoading = true)
        )

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            categoryUseCase.delete(categoryId)
        }
    }
}