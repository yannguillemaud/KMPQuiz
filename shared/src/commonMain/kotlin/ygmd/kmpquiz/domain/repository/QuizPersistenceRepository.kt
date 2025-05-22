package ygmd.kmpquiz.domain.repository

import ygmd.kmpquiz.domain.pojo.Quiz

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

    override fun delete(quiz: Quiz) {
        quizzes.remove(quiz)
    }
}