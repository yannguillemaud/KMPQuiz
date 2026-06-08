package ygmd.kmpquiz.domain.model.effect

interface UserEffect {
    object NavigateBack: UserEffect
    data class ShowSnackbar(val message: String): UserEffect
}