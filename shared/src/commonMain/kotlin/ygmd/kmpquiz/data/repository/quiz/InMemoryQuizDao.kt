package ygmd.kmpquiz.data.repository.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ygmd.kmpquiz.domain.dao.QuizDao
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import ygmd.kmpquiz.domain.entities.quiz.Quiz
import java.util.UUID

class InMemoryQuizDao: QuizDao {
    private val quizzesMap = mutableMapOf<String, Quiz>()
    private val _quizzesFlow = MutableStateFlow<List<Quiz>>(emptyList())

    override fun observeAllQuizzes(): Flow<List<Quiz>> {
        return _quizzesFlow.asStateFlow()
    }

    override fun getAllQuizzes(): List<Quiz> {
        return quizzesMap.values.toList()
    }

    override fun getQuizById(id: String): Quiz? {
        return quizzesMap[id]
    }

    override fun insertDraft(draftQuiz: DraftQuiz): String? {
        val newQuiz = Quiz(
            id = UUID.randomUUID().toString(),
            title = draftQuiz.title,
            qandas = draftQuiz.qandas,
            quizCron = draftQuiz.cron
        )

        quizzesMap[newQuiz.id] = newQuiz
        updateFlow()
        return newQuiz.id
    }

    override fun deleteById(id: String) {
        quizzesMap.remove(id)
        updateFlow()
    }

    override fun updateQuiz(quizId: String, quiz: Quiz) {
        quizzesMap[quizId] = quiz
        updateFlow()
    }

    private fun updateFlow() {
        _quizzesFlow.value = quizzesMap.values
            .toList()
    }
}