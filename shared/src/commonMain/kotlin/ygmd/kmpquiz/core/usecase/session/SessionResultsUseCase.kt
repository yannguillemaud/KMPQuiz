package ygmd.kmpquiz.core.usecase.session

import ygmd.kmpquiz.core.domain.qanda.AnswerContent
import ygmd.kmpquiz.core.domain.qanda.Qanda
import ygmd.kmpquiz.core.domain.session.AnswerId
import ygmd.kmpquiz.core.domain.session.CategoryStat
import ygmd.kmpquiz.core.domain.session.QandaId
import ygmd.kmpquiz.core.domain.session.Session
import ygmd.kmpquiz.core.domain.session.SessionResults
import ygmd.kmpquiz.core.domain.session.SessionStats
import ygmd.kmpquiz.core.usecase.qanda.GetQandaUseCase

class SessionResultsUseCase(
    private val qandaUseCase: GetQandaUseCase,
) {
    suspend operator fun invoke(session: Session): SessionResults {
        val sessionId = session.sessionId.id
        check(session.state is Session.SessionState.Completed) { "Session [$sessionId] is not completed" }
        val answers = session.answers.mapNotNull { (qandaId, answerId) ->
            val qanda = qandaUseCase.getById(qandaId.id) ?: return@mapNotNull null
            val answer =
                qanda.answers.firstOrNull { it.id == answerId.id } ?: return@mapNotNull null
            qanda to answer
        }
        val stats = computeStats(answers)
        val userAnswers = answers.associate { (qanda, answer) ->
            QandaId(qanda.id) to SessionResults.UserAnswer(
                AnswerId(answer.id),
                qanda.answers.correctAnswer == answer
            )
        }
        return SessionResults(userAnswers, stats)
    }

    private fun computeStats(answers: List<Pair<Qanda, AnswerContent>>): SessionStats {
        val answersByCategory: Map<String, Pair<Int, Int>> = answers.groupBy { it.first.categoryId }
            .mapValues { (_, answers) ->
                val (correct, incorrect) = answers.partition { (qanda, answer) ->
                    qanda.answers.correctAnswer.id == answer.id
                }
                correct.size to incorrect.size
            }
        return SessionStats(
            totalQuestions = answersByCategory.values.sumOf { (correctAnswers, incorrectAnswers) -> correctAnswers + incorrectAnswers },
            totalCorrect = answersByCategory.values.sumOf { it.first },
            totalIncorrect = answersByCategory.values.sumOf { it.second },
            categoriesStats = answersByCategory.map { (categoryId, results) ->
                val (correct, incorrect) = results
                CategoryStat(
                    categoryId = categoryId,
                    totalQuestions = correct + incorrect,
                    correctAnswers = correct,
                    incorrectAnswers = incorrect
                )
            }
        )
    }
}