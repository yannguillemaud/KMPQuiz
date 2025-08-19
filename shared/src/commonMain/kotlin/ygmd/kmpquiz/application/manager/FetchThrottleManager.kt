package ygmd.kmpquiz.application.manager

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val logger = Logger.withTag("FetchThrottleManager")

class FetchThrottleManager(
    private val throttleDuration: Duration = 5.seconds,
    private val scope: CoroutineScope,
) {
    private var throttleJob: Job? = null
    private val _canExecute = MutableStateFlow(true)

    val canExecute: StateFlow<Boolean> = _canExecute.asStateFlow()
    fun isExecutionAllowed(): Boolean = _canExecute.value

    suspend fun executeIfAllowed(action: suspend () -> Unit): Boolean {
        if(isExecutionAllowed().not()){
            logger.w { "Action blocked by throttling" }
            return false
        }

        action()
        startThrottleTimer()
        return true
    }

    private fun startThrottleTimer() {
        throttleJob?.cancel()
        _canExecute.value = false
        throttleJob = scope.launch {
            delay(throttleDuration)
            _canExecute.value = true
            logger.d { "Throttle timer expired, action available" }
        }
    }
}