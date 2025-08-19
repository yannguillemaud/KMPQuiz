package ygmd.kmpquiz.android

import kotlinx.serialization.Serializable

// Routes de navigation
@Serializable
object Home

@Serializable
object Fetch

@Serializable
object Saved

@Serializable
object Settings

@Serializable
object Quizzes

@Serializable
object QuizCreation

@Serializable
data class PlayQuiz(val quizId: String)