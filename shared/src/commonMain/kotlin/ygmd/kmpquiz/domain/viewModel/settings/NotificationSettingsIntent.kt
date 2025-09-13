package ygmd.kmpquiz.domain.viewModel.settings

import ygmd.kmpquiz.domain.entities.cron.QuizCron
import ygmd.kmpquiz.domain.entities.quiz.Quiz

sealed interface NotificationSettingsIntent {
    data class ToggleCron(val quiz: Quiz, val cronSetting: QuizCron) : NotificationSettingsIntent
    data class UpdateCron(val quiz: Quiz, val cronSetting: QuizCron) : NotificationSettingsIntent
    data class DeleteCron(val quiz: Quiz) : NotificationSettingsIntent
}