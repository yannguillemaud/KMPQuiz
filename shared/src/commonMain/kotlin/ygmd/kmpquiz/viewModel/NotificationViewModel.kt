package ygmd.kmpquiz.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.usecase.NotificationUseCase

class NotificationTestViewModel(
    private val notificationUseCase: NotificationUseCase
) : ViewModel() {

    fun generateNotifications() {
        viewModelScope.launch {
            try {
                notificationUseCase.generateAndScheduleNotifications()
                println("✅ Notifications générées avec succès")
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
            }
        }
    }

    fun cancelAllNotifications() {
        viewModelScope.launch {
            try {
                notificationUseCase.cancelAllNotifications()
                println("✅ Notifications annulées")
            } catch (e: Exception) {
                println("❌ Erreur: ${e.message}")
            }
        }
    }
}