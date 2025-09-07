package ygmd.kmpquiz.domain.entities.qanda

import kotlinx.serialization.Serializable

@Serializable
sealed interface Question {
    enum class QuestionType(val value: String){
        TEXT("text"),
        IMAGE("image")
    }
    val contextKey: String

    val type: String
        get() = when(this){
            is TextQuestion -> QuestionType.TEXT.value
            is ImageQuestion -> QuestionType.IMAGE.name
        }

    @Serializable
    data class TextQuestion(val text: String) : Question {
        override val contextKey: String
            get() = text
    }

    @Serializable
    data class ImageQuestion(
        val imageUrl: String,
        val text: String? = null,
    ) : Question {
        override val contextKey: String
            get() = imageUrl + text.orEmpty()
    }
}