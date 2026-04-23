package ygmd.kmpquiz.domain.model.category

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String
)

data class CategoryWithCount(
    val id: String,
    val name: String,
    val questionsCount: Int,
)