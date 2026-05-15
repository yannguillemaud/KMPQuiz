package ygmd.kmpquiz.domain.model.cron

import kotlinx.serialization.Serializable
import ygmd.kmpquiz.domain.service.CronSchedulerHelper

@Serializable
data class QuizCron(
    val id: String,
    val name: String, // not useful
    val selection: SchedulerSelection?,
    val expression: String,
    val isEnabled: Boolean = false,
)

@Serializable
data class SchedulerConfiguration(
    val id: String,
    val selection: SchedulerSelection,
    val isEnabled: Boolean
){
    val cron: String = CronSchedulerHelper.selectionAsCron(selection)
}