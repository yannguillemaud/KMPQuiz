package ygmd.kmpquiz.core.domain.qanda

import ygmd.kmpquiz.core.domain.category.Category

data class QandaWithCategory(
    val qanda: Qanda,
    val category: Category,
)