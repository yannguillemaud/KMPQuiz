package ygmd.kmpquiz.domain.entities.qanda

sealed interface Choice {
    val contextKey: String

    data class TextChoice(
        val text: String
    ): Choice {
        override val contextKey = text
    }

    data class ImageChoice(
        val imageUrl: String,
        val altText: String? = null
    ) : Choice {
        override val contextKey = imageUrl
    }
}