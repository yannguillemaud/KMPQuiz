package ygmd.kmpquiz.infra.theTriviaApi

import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QuestionType

object TheTriviaApiMapper {
    fun mapToDomain(quizResponse: TheTriviaApiResponse): Qanda = with(quizResponse) {
        return Qanda(
            id = null,
            category = category,
            difficulty = difficulty,
            question = QuestionType.TextQuestion(this.question.text),
            answers = when (type) {
                "text_choice" -> typedIncorrectAnswers
                    .map { textOf(it) }

                "image_choice" -> TODO()
                else -> error("Type not handled: $type")
            }
        )
    }

    private fun TheTriviaApiResponse.textOf(it: TriviaAnswer): AnswerContent.TextAnswer {
        val value = (it as TriviaAnswer.TextAnswer).value
        return AnswerContent.TextAnswer(
            value,
            isCorrect = value == correctAnswer
        )
    }
}