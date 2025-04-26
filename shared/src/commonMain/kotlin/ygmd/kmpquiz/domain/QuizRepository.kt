package ygmd.kmpquiz.domain

interface QuizRepository {
    fun findAll(): List<Quiz>
    fun save(quiz: Quiz)
    fun saveAll(quizs: List<Quiz>)
}