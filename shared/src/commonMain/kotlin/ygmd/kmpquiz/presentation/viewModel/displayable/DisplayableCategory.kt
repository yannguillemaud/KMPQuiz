package ygmd.kmpquiz.presentation.viewModel.displayable

data class DisplayableCategory(
    val id: String,
    val name: String,
)

data class DisplayableCategoryWithCount(
    val id: String,
    val name: String,
    val questionsCount: Int,
)
