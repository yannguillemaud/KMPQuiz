package ygmd.kmpquiz.domain.repository

import ygmd.kmpquiz.domain.pojo.Quiz

interface QuizRepository {
    fun findAll(): List<Quiz>
    fun save(quiz: Quiz)
    fun saveAll(quizs: List<Quiz>)
    fun delete(quiz: Quiz)
}