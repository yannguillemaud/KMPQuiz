package ygmd.kmpquiz.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String
)
