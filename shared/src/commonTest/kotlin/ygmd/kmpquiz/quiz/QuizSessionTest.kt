package ygmd.kmpquiz.quiz

import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.createQuizSession
import ygmd.kmpquiz.domain.pojo.quiz.QuizSession
import kotlin.test.Test

class QuizSessionTest {

    private val sampleQandas = listOf(
        createInternalQanda(id = 1L, question = "Q1"),
        createInternalQanda(id = 2L, question = "Q2"),
        createInternalQanda(id = 3L, question = "Q3")
    )

    @Test
    fun `currentQanda should return correct question based on index`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 1)

        // When & Then
        assertThat(session.currentQanda?.question).isEqualTo("Q2")
    }

    @Test
    fun `currentQanda should return null when index out of bounds`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 5)

        // When & Then
        assertThat(session.currentQanda).isNull()
    }

    @Test
    fun `isComplete should be false for valid questions in progress`() {
        // Given
        val session1 = QuizSession(qandas = sampleQandas, currentIndex = 0) // Première question
        val session2 = QuizSession(qandas = sampleQandas, currentIndex = 1) // Deuxième question
        val session3 = QuizSession(qandas = sampleQandas, currentIndex = 2) // Dernière question

        // When & Then
        assertThat(session1.isComplete).isFalse()
        assertThat(session2.isComplete).isFalse()
        assertThat(session3.isComplete).isFalse()
    }

    @Test
    fun `isComplete should be true when all questions answered`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 3) // Index == size

        // When & Then
        assertThat(session.isComplete).isTrue()
    }

    @Test
    fun `isComplete should be true when index beyond bounds`() {
        // Given
        val session = createQuizSession(
            qandas = sampleQandas,
            currentIndex = 3
        )

        // When & Then
        assertThat(session.isComplete).isTrue()
    }

    @Test
    fun `isComplete logic verification`() {
        // Given
        val session1 = QuizSession(qandas = sampleQandas, currentIndex = 2) // Dernière question valide (index 2 < size 3)
        val session2 = QuizSession(qandas = sampleQandas, currentIndex = 3) // Toutes questions terminées (index 3 == size 3)
        val session3 = QuizSession(qandas = sampleQandas, currentIndex = 3) // Au-delà (index 4 > size 3)

        // When & Then
        assertThat(session1.isComplete).isFalse() // Encore une question valide à afficher
        assertThat(session2.isComplete).isTrue()  // Toutes les questions ont été vues
        assertThat(session3.isComplete).isTrue()  // Au-delà de la fin
    }

    @Test
    fun `should handle empty qandas list`() {
        // Given
        val session = QuizSession(qandas = emptyList(), currentIndex = 0)

        // When & Then
        assertThat(session.currentQanda).isNull()
        assertThat(session.isComplete).isTrue()
    }

    @Test
    fun `edge cases for single question quiz`() {
        // Given
        val singleQuestion = listOf(createInternalQanda(id = 1L, question = "Single Q"))
        val session1 = QuizSession(qandas = singleQuestion, currentIndex = 0) // Question courante
        val session2 = QuizSession(qandas = singleQuestion, currentIndex = 1) // Question terminée

        // When & Then
        assertThat(session1.currentQanda?.question).isEqualTo("Single Q")
        assertThat(session1.isComplete).isFalse()

        assertThat(session2.currentQanda).isNull()
        assertThat(session2.isComplete).isTrue()
    }
}