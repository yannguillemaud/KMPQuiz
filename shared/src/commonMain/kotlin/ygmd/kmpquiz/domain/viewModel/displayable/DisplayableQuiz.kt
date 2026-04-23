package ygmd.kmpquiz.domain.viewModel.displayable

data class DisplayableQuiz(
    val id: String,
    val title : String = "",
    val questionsSize: Int = 0,
    val categories: List<DisplayableCategory> = emptyList(),
    val cron: DisplayableQuizCron? = null,
)