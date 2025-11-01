package ygmd.kmpquiz.domain.viewModel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.state.UiState

class CategoryViewModel(
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    val categories: StateFlow<UiState<List<DisplayableCategory>>> =
        categoryUseCase.observeCategories()
            .map { categories ->
                UiState.Success(categories.map { category ->
                    DisplayableCategory(category.id, category.name)
                })
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )
}