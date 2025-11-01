package ygmd.kmpquiz.domain.viewModel.qandas.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.usecase.category.CategoryUseCase
import ygmd.kmpquiz.domain.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.qanda.GetQandaUseCase
import ygmd.kmpquiz.domain.usecase.qanda.SaveQandasUseCase
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQanda
import ygmd.kmpquiz.domain.viewModel.error.UiError
import ygmd.kmpquiz.domain.viewModel.state.UiState

class SavedQandasViewModel(
    private val getQandaUseCase: GetQandaUseCase,
    private val saveQandaUseCase: SaveQandasUseCase,
    private val deleteQandasUseCase: DeleteQandasUseCase,
    private val categoryUseCase: CategoryUseCase,
) : ViewModel() {
    private val _categoryFilter = MutableStateFlow<QandaFilter?>(null)
    val savedState: StateFlow<UiState<List<DisplayableQanda>>> = combine(
        getQandaUseCase.observeSaved(),
        categoryUseCase.observeCategories(),
        _categoryFilter
    ) { qandas, categories, filter ->
        val categoriesById = categories.associateBy { it.id }
        val filteredQandas =
            if (filter != null) qandas.filter { filter.apply(it) }
            else qandas
        val qandasWithCategory = filteredQandas.map { qanda ->
            val category = categoriesById[qanda.categoryId] ?: throw IllegalStateException()
            DisplayableQanda(
                id = qanda.id,
                contextKey = qanda.contextKey,
                category = DisplayableCategory(category.id, category.name),
                question = qanda.question,
                answers = qanda.answers
            )
        }
        UiState.Success(qandasWithCategory)
    }
        .catch { UiState.Error(null, UiError.LoadQandaFailed) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )

    val availableCategories: StateFlow<UiState<List<DisplayableCategory>>> =
        categoryUseCase.observeCategories()
            .map { categories ->
                UiState.Success(data = categories.map { DisplayableCategory(it.id, it.name) })
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    fun processPersistenceIntent(persistanceIntent: PersistanceIntent) {
        when (persistanceIntent) {
            is PersistanceIntent.DeleteQanda -> {
                deleteQanda(persistanceIntent.qandaId)
            }

            is PersistanceIntent.SaveAll -> {
                saveAll(persistanceIntent.qandas)
            }

            is PersistanceIntent.Save -> {
                save(persistanceIntent.qanda)
            }

            is PersistanceIntent.DeleteCategory -> deleteCategory(persistanceIntent.categoryId)
        }
    }

    fun processFilterIntent(filterIntent: QandaFilterIntent) {
        when (filterIntent) {
            is QandaFilterIntent.CategoryFilter -> _categoryFilter.value =
                QandaFilter.CategoryFilter(filterIntent.categoryId)
        }
    }

    private fun saveAll(qandas: List<DraftQanda>) {
        viewModelScope.launch {
            qandas.forEach { saveQandaUseCase.save(it) }
        }
    }

    private fun save(qanda: DraftQanda) {
        viewModelScope.launch {
            saveQandaUseCase.save(qanda)
        }
    }

    private fun deleteQanda(qandaId: String) {
        viewModelScope.launch {
            deleteQandasUseCase.deleteById(qandaId)
        }
    }

    private fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            categoryUseCase.delete(categoryId)
            deleteQandasUseCase.deleteByCategory(categoryId)
        }
    }
}