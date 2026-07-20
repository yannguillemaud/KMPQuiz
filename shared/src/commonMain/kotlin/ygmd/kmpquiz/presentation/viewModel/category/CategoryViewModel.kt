package ygmd.kmpquiz.presentation.viewModel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategoryWithCount

/**
 * A pending bulk deletion awaiting user confirmation.
 *
 * @param categoryIds ids queued for deletion.
 * @param categoryNames names aligned with [categoryIds], used to build the dialog wording.
 * @param affectedQuizCount number of DISTINCT quizzes that reference any of these categories
 *   (a quiz shared across several selected categories is counted once).
 */
data class PendingCategoriesDeletion(
    val categoryIds: List<String>,
    val categoryNames: List<String>,
    val affectedQuizCount: Int,
)

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<DisplayableCategoryWithCount> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val pendingDeletion: PendingCategoriesDeletion? = null,
) {
    /**
     * Derived (not a separate flow) so it can never drift out of sync with [selectedIds]:
     * selection mode is active precisely when at least one item is selected.
     */
    val selectionMode: Boolean get() = selectedIds.isNotEmpty()
}

private val logger = Logger.withTag("CategoryViewModel")

class CategoryViewModel(
    private val categoryUseCase: CategoryUseCase
) : ViewModel() {
    private val _selectedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _pendingDeletion = MutableStateFlow<PendingCategoriesDeletion?>(null)

    val categories = combine(
        categoryUseCase.observeCategories(),
        _selectedIds,
        _pendingDeletion,
    ) { allCategories, selectedIds, pending ->
        val categories = allCategories.map {
            DisplayableCategoryWithCount(
                it.id,
                it.name,
                it.questionsCount
            )
        }
        CategoryUiState(
            categories = categories,
            selectedIds = selectedIds,
            pendingDeletion = pending,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CategoryUiState(isLoading = true)
        )

    /**
     * Enters selection mode (long-press) and selects the pressed item.
     */
    fun enterSelection(categoryId: String) {
        _selectedIds.value = _selectedIds.value + categoryId
    }

    /**
     * Toggles the selection of an item while in selection mode. Removing the last selected
     * item leaves [selectedIds] empty, which auto-exits selection mode via the derived
     * [CategoryUiState.selectionMode].
     */
    fun toggleSelection(categoryId: String) {
        val current = _selectedIds.value
        _selectedIds.value =
            if (categoryId in current) current - categoryId else current + categoryId
    }

    /**
     * Leaves selection mode without deleting anything (the ✕ contextual action).
     */
    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    /**
     * Opens the confirmation dialog for the current selection. Looks up the selected names
     * and queries how many DISTINCT quizzes still reference the set so the dialog can warn
     * before a cascade delete. Early-returns on an empty selection: an empty SQL `IN ()`
     * clause is a syntax error.
     */
    fun requestDeleteSelected() {
        val ids = _selectedIds.value
        if (ids.isEmpty()) return
        val names = categories.value.categories
            .filter { it.id in ids }
            .map { it.name }
        viewModelScope.launch {
            val affectedQuizCount = categoryUseCase.countCategoriesInUse(ids)
            _pendingDeletion.value = PendingCategoriesDeletion(
                categoryIds = ids.toList(),
                categoryNames = names,
                affectedQuizCount = affectedQuizCount,
            )
        }
    }

    /**
     * Confirms the pending deletion: deletes each category (cascading it out of any quizzes
     * that reference it), logging per-failure. Clears the selection and closes the dialog
     * once done. No atomic transaction is required by the spec, so this reuses the existing
     * single-id [CategoryUseCase.delete] in a loop rather than a dedicated bulk use case.
     */
    fun confirmDeleteSelected() {
        val pending = _pendingDeletion.value ?: return
        viewModelScope.launch {
            pending.categoryIds.forEach { id ->
                categoryUseCase.delete(id)
                    .onFailure { logger.w(it) { "Failed to delete category $id" } }
            }
            _selectedIds.value = emptySet()
            _pendingDeletion.value = null
        }
    }

    /**
     * Dismisses the confirmation dialog without deleting anything, keeping the current
     * selection intact so the user can adjust it.
     */
    fun cancelDeletion() {
        _pendingDeletion.value = null
    }
}
