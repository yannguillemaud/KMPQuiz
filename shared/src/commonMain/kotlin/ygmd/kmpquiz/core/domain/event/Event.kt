package ygmd.kmpquiz.core.domain.event

sealed interface Event {
    data object NavigateBack: Event
    data class ShowSnackbar(val message: String): Event
}