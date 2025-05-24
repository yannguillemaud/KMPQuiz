package ygmd.kmpquiz.domain.pojo

data class Quiz(
    val category: String,
    val qandas: List<InternalQanda> = emptyList()
)