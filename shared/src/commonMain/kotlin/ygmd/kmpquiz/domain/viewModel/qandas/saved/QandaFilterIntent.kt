package ygmd.kmpquiz.domain.viewModel.qandas.saved

import ygmd.kmpquiz.domain.model.qanda.Qanda

sealed interface QandaFilterIntent {
    data class CategoryFilter(val categoryId: String): QandaFilterIntent
}

sealed interface QandaFilter {
    fun apply(qanda: Qanda): Boolean

    data class CategoryFilter(val categoryId: String): QandaFilter {
        override fun apply(qanda: Qanda): Boolean {
            return qanda.categoryId == categoryId
        }
    }
}