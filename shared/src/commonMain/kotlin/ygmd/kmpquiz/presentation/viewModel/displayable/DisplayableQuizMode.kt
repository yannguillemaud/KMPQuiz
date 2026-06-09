package ygmd.kmpquiz.presentation.viewModel.displayable

sealed interface DisplayableQuizMode {
    data object Full : DisplayableQuizMode
    data class Limited(val limit: Int = 0, val max: Int = 0) : DisplayableQuizMode
    data class ByCategory(val limits: Map<DisplayableCategoryWithCount, Int> = emptyMap()) :
        DisplayableQuizMode
}