package ygmd.kmpquiz.domain.result

sealed interface DeleteResult {
    data object Success : DeleteResult

    sealed interface Failure : DeleteResult
    data class NotFound(val notFoundIds: List<String>) : Failure
    data class GenericError(val error: Throwable) : Failure
}