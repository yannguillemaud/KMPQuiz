package ygmd.kmpquiz.viewModel.settings

import ygmd.kmpquiz.domain.entities.quiz.Quiz

sealed interface NotificationSettingsIntent {
    data class ToggleCron(val quiz: Quiz, val cronSetting: UiCronSetting) : NotificationSettingsIntent
    data class UpdateCron(val quiz: Quiz, val cronSetting: UiCronSetting) : NotificationSettingsIntent
    data class DeleteCron(val quiz: Quiz) : NotificationSettingsIntent
}