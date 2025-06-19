package ygmd.kmpquiz.infra.theTriviaApi

import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.qanda.Image
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QuestionType

object TheTriviaApiMapper {
    fun mapToDomain(
        apiQuestion:
    ): Qanda {
        return try {
            val questionType = mapQuestionType(
                apiQuestion.question,
                apiQuestion.type
            )
            val answers = mapAnswers(
                correctAnswer = apiQuestion.correctAnswer,
                incorrectAnswer = apiQuestion.incorrectAnswers,
                questionType = apiQuestion.type
            )

        }
    }
}