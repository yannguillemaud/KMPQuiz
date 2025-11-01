package ygmd.kmpquiz.domain.model.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val difficulty: String? = null,
    val tags: Map<String, String> = emptyMap()
)