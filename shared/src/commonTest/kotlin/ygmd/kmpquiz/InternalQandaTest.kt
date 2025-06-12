package ygmd.kmpquiz

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import ygmd.kmpquiz.domain.pojo.InternalQanda

class InternalQandaTest {

    @Test
    fun `contentKey should be normalized and lowercase`() {
        // Given
        val qanda = InternalQanda(
            id = 1L,
            category = "Test",
            question = "  What Is The Capital?  ",
            answers = listOf("A", "B"),
            correctAnswer = "  PARIS  ",
            difficulty = "Easy"
        )

        // When
        val contentKey = qanda.contentKey

        // Then
        assertThat(contentKey).isEqualTo("what is the capital?|paris")
    }

    @Test
    fun `contentKey should handle special characters`() {
        // Given
        val qanda = InternalQanda(
            id = 1L,
            category = "Test",
            question = "What's 2+2?",
            answers = listOf("3", "4"),
            correctAnswer = "4",
            difficulty = "Easy"
        )

        // When
        val contentKey = qanda.contentKey

        // Then
        assertThat(contentKey).isEqualTo("what's 2+2?|4")
    }

    @Test
    fun `contentKey should be consistent for same content`() {
        // Given
        val qanda1 = InternalQanda(
            id = 1L,
            category = "Math",
            question = "What is 2+2?",
            answers = listOf("3", "4", "5"),
            correctAnswer = "4",
            difficulty = "Easy"
        )

        val qanda2 = InternalQanda(
            id = 2L, // ID différent
            category = "Arithmetic", // Catégorie différente
            question = "What is 2+2?", // Même question
            answers = listOf("4", "5", "6"), // Réponses différentes
            correctAnswer = "4", // Même bonne réponse
            difficulty = "Medium" // Difficulté différente
        )

        // When & Then
        assertThat(qanda1.contentKey).isEqualTo(qanda2.contentKey)
    }
}