package ygmd.kmpquiz

import ygmd.kmpquiz.domain.entities.qanda.InternalQanda
import kotlin.test.Test
import kotlin.test.assertEquals

class InternalQandaTest {

    @Test
    fun `contentKey should be normalized and lowercase`() {
        // Given
        val qanda = createQanda()

        // When
        val contentKey = qanda.contextKey

        // Then
        assertEquals("what is the capital of france?|paris", contentKey)
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
        assertEquals("what's 2+2?|4", contentKey)
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
        assertEquals(qanda1.contentKey, qanda2.contentKey)
    }
}