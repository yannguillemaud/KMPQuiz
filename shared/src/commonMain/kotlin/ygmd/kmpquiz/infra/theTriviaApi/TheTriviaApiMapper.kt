package ygmd.kmpquiz.infra.theTriviaApi

import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QandaMetadata
import ygmd.kmpquiz.domain.entities.qanda.QuestionContent

/**
 * TODO
 * Since TheTriviaApi cannot fetch qandas with image, the mapper will not be continued
 */
object TheTriviaApiMapper {
    fun mapToDomain(quizResponse: TheTriviaApiResponse): Qanda = with(quizResponse) {
        return Qanda(
            question = QuestionContent.TextContent(question.text),
            metadata = QandaMetadata(
                category = category,
                difficulty = difficulty,
            ),
            answers = when (type) {
                "text_choice" -> TODO()
                "image_choice" -> TODO()
                else -> error("Type not handled: $type")
            }
        )
    }
}