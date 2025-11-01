package ygmd.kmpquiz.domain.model.qanda

import kotlinx.serialization.Serializable

@Serializable
data class Answers(
    val choices: List<Choice>,
    val correctAnswer: Choice,
): List<Choice> by choices {
    val contextKey = correctAnswer.contextKey
    fun shuffled(): Answers = copy(choices = choices.shuffled())
    val incorrectAnswers = choices.filter { it != correctAnswer }
}

object AnswersFactory {
    fun createMultipleTextChoices(correctAnswer: String, incorrectAnswers: List<String>): Answers {
        val incorrectAnswers = incorrectAnswers.map { Choice.TextChoice(it) }
        val correctAnswer = Choice.TextChoice(correctAnswer)
        return Answers(incorrectAnswers + correctAnswer, correctAnswer)
    }

    fun createTrueFalse(correctAnswer: Boolean): Answers {
        val correctAnswerText =
            Choice.TextChoice(correctAnswer.toString().replaceFirstChar { it.uppercase() })
        val incorrectAnswerText =
            Choice.TextChoice(correctAnswer.not().toString().replaceFirstChar { it.uppercase() })
        return Answers(listOf(correctAnswerText, incorrectAnswerText), correctAnswerText)
    }
}