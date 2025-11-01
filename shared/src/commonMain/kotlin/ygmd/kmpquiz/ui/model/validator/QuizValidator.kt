package ygmd.kmpquiz.ui.model.validator

object QuizValidator {
    fun validateTitle(title: String): String? = when {
        title.isEmpty() -> "Required"
        title.length > 15 -> "Title is too long"
        else -> null
    }

    fun validateCategories(selectedCategories: Set<String>): String? =
        if (selectedCategories.isEmpty()) {
            "At least one category must be selected"
        } else null
}