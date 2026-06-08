package ygmd.kmpquiz.domain.notifier

interface QuizNotifier {
    fun showQuizReminder(quizId: String)
}