package ygmd.kmpquiz.domain.entities.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val category: String,
    val difficulty: String?,
    val tags: Map<String, String> = emptyMap()
)