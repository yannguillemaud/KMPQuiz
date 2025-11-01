package ygmd.kmpquiz.domain.viewModel.qandas.saved

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda

sealed interface PersistanceIntent {
    data class SaveAll(val qandas: List<DraftQanda>) : PersistanceIntent
    data class Save(val qanda: DraftQanda): PersistanceIntent
    data class DeleteQanda(val qandaId: String) : PersistanceIntent
    data class DeleteCategory(val categoryId: String) : PersistanceIntent
}