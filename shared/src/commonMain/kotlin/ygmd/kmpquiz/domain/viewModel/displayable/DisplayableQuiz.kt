package ygmd.kmpquiz.domain.viewModel.displayable

import ygmd.kmpquiz.domain.model.cron.SchedulerSelection

data class DisplayableQuiz(
    val id: String,
    val title : String = "",
    val questionsSize: Int = 0,
    val categories: List<DisplayableCategory> = emptyList(),
    val scheduler: SchedulerSelection? = null,
    val isScheduled: Boolean = false,
)