package infra.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ygmd.kmpquiz.domain.usecase.notification.RescheduleAllQuizzesUseCase

class BootReceiver() : BroadcastReceiver(), KoinComponent {
    private val rescheduleUseCase by inject<RescheduleAllQuizzesUseCase>()

    override fun onReceive(context: Context, intent: Intent) {
        val isValidAction = intent.action == Intent.ACTION_BOOT_COMPLETED ||
                intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        if (!isValidAction) return
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                rescheduleUseCase()
            } catch (e: Exception) {
                // silent
            } finally {
                pendingResult.finish()
            }
        }
    }
}