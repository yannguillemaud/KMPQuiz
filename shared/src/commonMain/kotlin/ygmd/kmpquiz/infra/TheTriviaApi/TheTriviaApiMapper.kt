package ygmd.kmpquiz.infra.TheTriviaApi

import ygmd.kmpquiz.domain.entities.qanda.AnswerContent
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QuestionType

object TheTriviaApiMapper {
    fun map(
        dto: TheTriviaDto,
        questionType: QuestionType?,
        answerType: AnswerContent,
    ): Qanda {
        return Qanda(
            id = null,
            category = dto.category,
            difficulty = dto.difficulty,
            qandaQuestion = when(questionType){
                is QuestionType.TextQuestion -> QuestionType.TextQuestion(questionType.text)
            },
            answers = TODO()
        )
    }
}