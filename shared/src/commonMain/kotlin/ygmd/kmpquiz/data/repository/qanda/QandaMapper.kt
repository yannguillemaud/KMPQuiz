package ygmd.kmpquiz.data.repository.qanda

import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.entities.qanda.AnswersFactory
import ygmd.kmpquiz.domain.entities.qanda.Metadata
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.Question

class QandaMapper {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun map(qanda: ygmd.kmpquiz.database.Qanda): Qanda {
        val incorrectAnswers = json.decodeFromString<List<String>>(qanda.incorrect_answers_text)
        val isTrueFalse = incorrectAnswers.size == 1
        return Qanda(
            id = qanda.id,
            question = when(Question.QuestionType.valueOf(qanda.question_type)){
                Question.QuestionType.TEXT -> Question.TextQuestion(requireNotNull(qanda.question_text))
                Question.QuestionType.IMAGE -> Question.ImageQuestion(requireNotNull(qanda.question_url), qanda.question_text)
            },
            answers = if (isTrueFalse) AnswersFactory.createTrueFalse(qanda.correct_answer_text.toBoolean())
            else AnswersFactory.createMultipleTextChoices(
                qanda.correct_answer_text,
                incorrectAnswers
            ),
            metadata = Metadata(category = qanda.category, difficulty = null)
        )
    }
}