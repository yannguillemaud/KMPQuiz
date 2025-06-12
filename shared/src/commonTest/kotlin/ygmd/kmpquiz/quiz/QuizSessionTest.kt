package ygmd.kmpquiz.quiz

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.domain.pojo.QuizSession

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
    fun `isComplete should be true for last valid question`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 2) // Dernière question (index 2 sur 3 questions)

        // When & Then
        assertThat(session.isComplete).isTrue()
    }

    @Test
    fun `isComplete should be true when index beyond bounds`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 3) // Au-delà

        // When & Then
        assertThat(session.isComplete).isTrue()
    }

    @Test
    fun `isComplete should be false for non-last questions`() {
        // Given
        val session = QuizSession(qandas = sampleQandas, currentIndex = 0)

        // When & Then
        assertThat(session.isComplete).isFalse()
    }

    @Test
    fun `isComplete should be true only when index equals size`() {
        // Given
        val session1 = QuizSession(qandas = sampleQandas, currentIndex = 3) // == size
        val session2 = QuizSession(qandas = sampleQandas, currentIndex = 2) // < size
        val session3 = QuizSession(qandas = sampleQandas, currentIndex = 4) // > size

        // When & Then
        assertThat(session1.isComplete).isTrue()
        assertThat(session2.isComplete).isFalse()
        assertThat(session3.isComplete).isFalse()
    }

    @Test
    fun `should handle empty qandas list`() {
        // Given
        val session = QuizSession(qandas = emptyList(), currentIndex = 0)

        // When & Then
        assertThat(session.currentQanda).isNull()
        assertThat(session.isComplete).isTrue()
    }
}