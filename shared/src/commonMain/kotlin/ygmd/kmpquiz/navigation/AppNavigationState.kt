package ygmd.kmpquiz.navigation

import kotlinx.coroutines.flow.MutableStateFlow

data class InitialNavigationEvent(val quizId: String)

object AppNavigationState {
    val initialNavEvent = MutableStateFlow<InitialNavigationEvent?>(null)
    fun consumeNavEvent(){
        initialNavEvent.value = null
    }
}