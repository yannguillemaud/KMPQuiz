package ygmd.kmpquiz.domain.viewModel.fetch

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda

sealed interface FetchIntentAction {
    data object Fetch : FetchIntentAction
    data class SaveAll(val qandas: List<DraftQanda>) : FetchIntentAction
    data class Save(val qanda: DraftQanda) : FetchIntentAction
}