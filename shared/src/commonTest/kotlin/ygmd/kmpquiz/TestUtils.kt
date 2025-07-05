package ygmd.kmpquiz

import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.domain.entities.qanda.QuestionType
import ygmd.kmpquiz.domain.entities.qanda.toTextAnswers
import ygmd.kmpquiz.domain.entities.quiz.QuizSession

fun createQanda(
    id: Long? = 1L,
    category: String = "General",
    question: String = "What is the capital of France?",
    answers: List<String> = listOf("Paris", "London", "Berlin", "Madrid"),
    correctAnswer: String = "Paris",
    difficulty: String = "Easy"
) = Qanda(
    id = id,
    category = category,
    difficulty = difficulty,
    question = QuestionType.TextQuestion(question),
    answers = answers.toTextAnswers(correctAnswer)
)

fun createQuizSession(
    qandas: List<Qanda> = listOf(createQanda()),
    currentIndex: Int = 0,
    userAnswers: Map<Int, String> = emptyMap()
    // TODO replace with Qanda
) = QuizSession(qandas, currentIndex, userAnswers)

fun createMultiQuestionSession() = createQuizSession(
    qandas = listOf(
        createQanda(
            id = 1L,
            question = "What is 2+2?",
            answers = listOf("3", "4", "5", "6"),
            correctAnswer = "4",
            category = "Math",
            difficulty = "Easy"
        ),
        createQanda(
            id = 2L,
            question = "What is the largest planet?",
            answers = listOf("Earth", "Jupiter", "Saturn", "Mars"),
            correctAnswer = "Jupiter",
            category = "Science",
            difficulty = "Medium"
        ),
        createQanda(
            id = 3L,
            question = "Who wrote 1984?",
            answers = listOf("Orwell", "Huxley", "Bradbury", "Asimov"),
            correctAnswer = "Orwell",
            category = "Literature",
            difficulty = "Hard"
        )
    )
)