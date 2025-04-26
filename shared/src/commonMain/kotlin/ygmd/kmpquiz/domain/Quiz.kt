package ygmd.kmpquiz.domain

data class Quiz(
    val category: String,
    val qandas: List<QANDA> = emptyList()
)