package ygmd.kmpquiz

import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import ygmd.kmpquiz.domain.pojo.quiz.QuizSession

fun createInternalQanda(
    id: Long? = 1L,
    category: String = "General",
    question: String = "What is the capital of France?",
    answers: List<String> = listOf("Paris", "London", "Berlin", "Madrid"),
    correctAnswer: String = "Paris",
    difficulty: String = "Easy"
) = InternalQanda(
    id = id,
    category = category,
    question = question,
    answers = answers,
    correctAnswer = correctAnswer,
    difficulty = difficulty
)

fun createQuizSession(
    qandas: List<InternalQanda> = listOf(createInternalQanda()),
    currentIndex: Int = 0,
    userAnswers: Map<Int, String> = emptyMap()
) = QuizSession(qandas, currentIndex, userAnswers)

fun createMultiQuestionSession() = createQuizSession(
    qandas = listOf(
        createInternalQanda(
            id = 1L,
            question = "What is 2+2?",
            answers = listOf("3", "4", "5", "6"),
            correctAnswer = "4",
            category = "Math",
            difficulty = "Easy"
        ),
        createInternalQanda(
            id = 2L,
            question = "What is the largest planet?",
            answers = listOf("Earth", "Jupiter", "Saturn", "Mars"),
            correctAnswer = "Jupiter",
            category = "Science",
            difficulty = "Medium"
        ),
        createInternalQanda(
            id = 3L,
            question = "Who wrote 1984?",
            answers = listOf("Orwell", "Huxley", "Bradbury", "Asimov"),
            correctAnswer = "Orwell",
            category = "Literature",
            difficulty = "Hard"
        )
    )
)