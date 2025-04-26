package ygmd.kmpquiz.service.repository

import ygmd.kmpquiz.domain.Quiz
import ygmd.kmpquiz.domain.QuizRepository

class QuizPersistenceRepository: QuizRepository {
    private val quizzes = mutableListOf<Quiz>()

    override fun findAll(): List<Quiz> {
        return quizzes.toList()
    }

    override fun save(quiz: Quiz) {
        quizzes.add(quiz)
    }

    override fun saveAll(quizs: List<Quiz>) {
        quizzes.addAll(quizs)
    }
}