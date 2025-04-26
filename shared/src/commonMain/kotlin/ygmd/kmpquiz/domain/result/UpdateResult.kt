package ygmd.kmpquiz.domain.result

sealed interface UpdateResult {
    data object Success : UpdateResult

    sealed interface Failure : UpdateResult
    data class NotFound(val qandaId: String) : Failure
    data class GenericError(val error: Throwable) : Failure
}