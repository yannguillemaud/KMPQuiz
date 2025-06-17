package ygmd.kmpquiz.viewModel.save

import ygmd.kmpquiz.domain.entities.qanda.InternalQanda
import ygmd.kmpquiz.viewModel.error.ViewModelError

data class SaveQandasState(
    val savedQandas: List<InternalQanda> = emptyList(),
    val saveOperations: Map<String, SaveOperationState> = emptyMap(),
    val bulkOperation: BulkOperationState? = null,
    val error: ViewModelError? = null
){
    // États dérivés
    val totalSaved: Int get() = savedQandas.size
    val hasSavedQandas: Boolean get() = savedQandas.isNotEmpty()
    val isEmpty: Boolean get() = savedQandas.isEmpty() && bulkOperation == null && error == null

    // Opérations en cours
    val operationsInProgress: List<SaveOperationState>
        get() = saveOperations.values.filter { it.isInProgress }
    val hasOperationsInProgress: Boolean get() = operationsInProgress.isNotEmpty()

    // États pour l'UI
    val showProgress: Boolean get() = hasOperationsInProgress || bulkOperation?.isInProgress == true
    val canPerformBulkOperation: Boolean get() = bulkOperation?.isInProgress != true
    val hasError: Boolean get() = error != null

    // Helpers pour actions spécifiques
    fun isQandaBeingSaved(qandaKey: String): Boolean {
        return saveOperations[qandaKey]?.isInProgress == true
    }

    fun getQandaError(qandaKey: String): ViewModelError? {
        return (saveOperations[qandaKey] as? SaveOperationState.Error)?.error
    }
}

sealed class SaveOperationState(val contentKey: String){
    abstract val isInProgress: Boolean

    data class Saving(private val key: String): SaveOperationState(key){
        override val isInProgress: Boolean = true
    }

    data class Success(private val key: String): SaveOperationState(key){
        override val isInProgress: Boolean = false
    }

    data class Error(
        private val key: String,
        val error: ViewModelError
    ): SaveOperationState(key){
        override val isInProgress: Boolean = false
    }

    data class Deleting(private val key: String): SaveOperationState(key){
        override val isInProgress: Boolean = true
    }
}

sealed class BulkOperationState {
    abstract val isInProgress: Boolean
    abstract val type: BulkOperationType

    data class InProgress(
        override val type: BulkOperationType,
        val totalItems: Int,
        val processedItems: Int
    ): BulkOperationState(){
        override val isInProgress: Boolean = true
        val progress: Float get() =
            if(totalItems > 0) processedItems.toFloat() / totalItems
            else 0f
        val remainingItems: Int get() = totalItems - processedItems
    }

    data class Success(
        override val type: BulkOperationType,
        val error: ViewModelError,
        val successCount: Int = 0,
        val failureCount: Int = 0,
    ): BulkOperationState(){
        override val isInProgress: Boolean = false
        val hasPartialSuccess: Boolean get() = successCount > 0
    }
}

enum class BulkOperationType {
    SAVE_ALL,
    DELETE_ALL,
}