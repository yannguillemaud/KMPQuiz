package ygmd.kmpquiz.infra.theTriviaApi

import kotlinx.serialization.Serializable

/**
 * Modèle pour le contenu d'une question textuelle
 */
@Serializable
data class TextQuestion(
    val text: String
)

/**
 * Modèle pour le contenu d'une question avec image
 */
@Serializable
data class ImageAnswer(
    val url: String,
    val height: Long,
    val width: Long,
    val size: Long,
    val description: String
)

/**
 * Modèle principal pour une question de trivia de type text_choice
 * Compatible avec l'API The Trivia API v2
 */
@Serializable
data class TextChoiceQuestion(
    val id: String,
    val category: String,
    val type: String,
    val difficulty: String,
    val question: TextQuestion,
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val tags: List<String> = emptyList(),
    val regions: List<String> = emptyList(),
    val isNiche: Boolean
)

/**
 * Modèle principal pour une question de trivia de type image_choice
 * Compatible avec l'API The Trivia API v2
 */
@Serializable
data class ImageChoiceQuestion(
    val id: String,
    val category: String,
    val type: String,
    val difficulty: String,
    val question: TextQuestion,
    val correctAnswer: ImageAnswer,
    val incorrectAnswers: List<ImageAnswer>,
    val tags: List<String> = emptyList(),
    val regions: List<String> = emptyList(),
    val isNiche: Boolean
)

/**
 * Paramètres de requête pour l'API
 * Non-serializable car utilisé uniquement pour construire les query params
 */
data class ApiRequestParams(
    val limit: Int = 10,
    val categories: String? = null,
    val difficulty: String? = null,
    val tags: String? = null,
    val regions: String? = null,
    val types: String? = null
)

/**
 * Enum pour les types de questions supportés
 */
object QuestionTypes {
    const val TEXT_CHOICE = "text_choice"
    const val IMAGE_CHOICE = "image_choice"
}

/**
 * Enum pour les difficultés
 */
object Difficulties {
    const val EASY = "easy"
    const val MEDIUM = "medium"
    const val HARD = "hard"
}

/**
 * Enum pour les catégories disponibles
 */
object Categories {
    const val ARTS_AND_LITERATURE = "arts_and_literature"
    const val FILM_AND_TV = "film_and_tv"
    const val FOOD_AND_DRINK = "food_and_drink"
    const val GENERAL_KNOWLEDGE = "general_knowledge"
    const val GEOGRAPHY = "geography"
    const val HISTORY = "history"
    const val MUSIC = "music"
    const val SCIENCE = "science"
    const val SOCIETY_AND_CULTURE = "society_and_culture"
    const val SPORT_AND_LEISURE = "sport_and_leisure"
}