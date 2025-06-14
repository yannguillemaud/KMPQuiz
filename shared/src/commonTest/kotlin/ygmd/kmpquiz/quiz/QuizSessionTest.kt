package ygmd.kmpquiz.quiz

import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.createQuizSession
import ygmd.kmpquiz.domain.pojo.quiz.QuizSession
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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
        assertEquals("Q2", session.currentQanda?.question)
    }

    @Test
    fun `currentQanda should return null when index out of bounds`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 5)

        // When & Then
        assertNull(session.currentQanda)
    }

    @Test
    fun `isComplete should be false for valid questions in progress`() {
        // Given
        val session1 = QuizSession(qandas = sampleQandas, currentIndex = 0) // Première question
        val session2 = QuizSession(qandas = sampleQandas, currentIndex = 1) // Deuxième question
        val session3 = QuizSession(qandas = sampleQandas, currentIndex = 2) // Dernière question

        // When & Then
        assertFalse { session1.isComplete }
        assertFalse { session2.isComplete }
        assertFalse { session3.isComplete }
    }

    @Test
    fun `isComplete should be true when all questions answered`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 3) // Index == size

        // When & Then
        assertTrue { session.isComplete }
    }

    @Test
    fun `isComplete should be true when index beyond bounds`() {
        // Given
        val session = createQuizSession(
            qandas = sampleQandas,
            currentIndex = 3
        )

        // When & Then
        assertTrue { session.isComplete }
    }

    @Test
    fun `isComplete logic verification`() {
        // Given
        val session1 = QuizSession(qandas = sampleQandas, currentIndex = 2) // Dernière question valide (index 2 < size 3)
        val session2 = QuizSession(qandas = sampleQandas, currentIndex = 3) // Toutes questions terminées (index 3 == size 3)
        val session3 = QuizSession(qandas = sampleQandas, currentIndex = 3) // Au-delà (index 4 > size 3)

        // When & Then
        assertFalse { session1.isComplete } // Encore une question valide à afficher
        assertTrue { session2.isComplete }  // Toutes les questions ont été vues
        assertTrue { session3.isComplete }  // Au-delà de la fin
    }

    @Test
    fun `should handle empty qandas list`() {
        // Given
        val session = QuizSession(qandas = emptyList(), currentIndex = 0)

        // When & Then
        assertNull(session.currentQanda)
        assertTrue { session.isComplete }
    }

    @Test
    fun `edge cases for single question quiz`() {
        // Given
        val singleQuestion = listOf(createInternalQanda(id = 1L, question = "Single Q"))
        val session1 = QuizSession(qandas = singleQuestion, currentIndex = 0) // Question courante
        val session2 = QuizSession(qandas = singleQuestion, currentIndex = 1) // Question terminée

        // When & Then
        assertEquals("Single Q", session1.currentQanda?.question)
        assertFalse { session1.isComplete }

        assertNull(session2.currentQanda)
        assertTrue { session2.isComplete }
    }
}