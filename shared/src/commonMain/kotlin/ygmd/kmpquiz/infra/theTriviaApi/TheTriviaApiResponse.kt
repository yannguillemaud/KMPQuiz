package ygmd.kmpquiz.infra.theTriviaApi

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Modèle pour le contenu d'une question textuelle
 */
@Serializable
data class Question(
    val text: String
)

@Serializable
data class TheTriviaApiResponse(
    val category: String,
    val id: String,
    val tags: List<String>,
    val difficulty: String,
    val regions: List<String>,
    val isNiche: Boolean,
    val question: Question,
    val correctAnswer: String, // Toujours un string même pour les images
    val incorrectAnswers: JsonElement, // On gère manuellement
    val type: String
) {
    // Propriété calculée pour obtenir les mauvaises réponses typées
    val typedIncorrectAnswers: List<Answer>
        get() = when (type) {
            "text_choice" -> {
                Json.decodeFromJsonElement<List<String>>(incorrectAnswers)
                    .map { Answer.TextAnswer(it) }
            }
            "image_choice" -> {
                Json.decodeFromJsonElement<List<Answer.ImageAnswer>>(incorrectAnswers)
            }
            else -> emptyList()
        }
}

@Serializable
sealed class Answer {
    @Serializable
    @SerialName("text_choice")
    data class TextAnswer(val value: String) : Answer()

    @Serializable
    @SerialName("image_choice")
    data class ImageAnswer(
        val url: String,
        val alt: String,
        val description: String? = null
    ) : Answer()
}