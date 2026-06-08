package ygmd.kmpquiz.events.event

sealed interface Event {
    data object NavBackEvent: Event
    data class SnackbarEvent(val message: String): Event
}