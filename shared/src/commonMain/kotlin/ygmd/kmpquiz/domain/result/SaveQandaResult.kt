package ygmd.kmpquiz.domain.result

import ygmd.kmpquiz.domain.model.qanda.Qanda


sealed interface SaveQandaResult {
    data class Success(val qanda: Qanda) : SaveQandaResult

    sealed interface Failure : SaveQandaResult
    data class AlreadyExists(val existingQandaId: String) : Failure
    data class Error(val error: Throwable) : Failure
}

sealed interface SaveMultipleQandasResult {
    data object Success : SaveMultipleQandasResult

    sealed interface Failure : SaveMultipleQandasResult
    data class AlreadyExist(val existingQandasIds: List<String>) : Failure
    data class GenericError(val error: Throwable) : Failure
}