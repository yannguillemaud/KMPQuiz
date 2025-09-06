package ygmd.kmpquiz.domain.entities.qanda

import kotlinx.serialization.Serializable

@Serializable
sealed interface Question {
    val text: String
    val contextKey: String

    @Serializable
    data class TextQuestion(override val text: String) : Question {
        override val contextKey: String
            get() = text
    }

    @Serializable
    data class ImageQuestion(
        override val text: String,
        val imageUrl: String,
        val altText: String? = null
    ) : Question {
        override val contextKey: String
            get() = "${text}|${imageUrl}"
    }
}