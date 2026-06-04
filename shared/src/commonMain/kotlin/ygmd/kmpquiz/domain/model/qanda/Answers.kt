package ygmd.kmpquiz.domain.model.qanda

import java.util.UUID

object AnswersFactory {
    fun createMultipleTextChoices(correctAnswer: String, incorrectAnswers: List<String>): Answers {
        val incorrectAnswers = incorrectAnswers.map { AnswerContent.TextAnswerContent(UUID.randomUUID().toString(), it) }
        val correctAnswer = AnswerContent.TextAnswerContent(UUID.randomUUID().toString(), correctAnswer)
        return Answers(incorrectAnswers + correctAnswer, correctAnswer)
    }
}