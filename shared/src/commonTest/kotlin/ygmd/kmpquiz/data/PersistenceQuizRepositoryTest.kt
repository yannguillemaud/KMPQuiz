package ygmd.kmpquiz.data

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.data.repository.quiz.QuizRepositoryImpl
import ygmd.kmpquiz.domain.dao.QuizDao
import ygmd.kmpquiz.domain.model.quiz.DraftQuiz
import ygmd.kmpquiz.domain.model.quiz.Quiz
import java.util.UUID
import kotlin.test.Test


@OptIn(ExperimentalCoroutinesApi::class) // Requis pour runTest dans certaines versions
class PersistenceQuizRepositoryTest {
    private val dao = mockk<QuizDao>()
    private val repository = QuizRepositoryImpl(dao)

    @Test
    fun `observeAll - empty database returns empty flow list`() = runTest {
        // Given
        every {
            dao.getAllQuizzes()
        } returns emptyList()

        // When
        val result = repository.observeAll().first()

        // Then
        result.shouldBeEmpty()
    }

    @Test
    fun `observeAll - emits list with one quiz after insert`() = runTest {
        // Given
        val id = UUID.randomUUID().toString()
        val draftQuiz = DraftQuiz(
            title = "Quiz Observé",
            qandas = emptyList(),
        )
        every {
            dao.insertDraft(any())
        } returns id
        every {
            dao.getAllQuizzes()
        } returns listOf(
            Quiz(
                id = id,
                title = draftQuiz.title,
                qandas = draftQuiz.qandas,
            )
        )

        // When
        val insertResult = repository.insertQuiz(draftQuiz)
        insertResult.isSuccess shouldBe true
        val insertedQuiz = insertResult.getOrThrow()

        val quizzesAfterInsert = repository.observeAll().first()
        quizzesAfterInsert shouldHaveSize 1
        quizzesAfterInsert.first().id shouldBe insertedQuiz.id
        quizzesAfterInsert.first().title shouldBe draftQuiz.title
        quizzesAfterInsert.first().qandas shouldBeEqual draftQuiz.qandas
    }

    @Test
    fun `observeAll - emits list with multiple quizzes after inserts`() = runTest {
        every {
            dao.insertDraft(any())
        } returns UUID.randomUUID().toString()
        every {
            dao.getAllQuizzes()
        } returns listOf(
            Quiz(
                id = UUID.randomUUID().toString(),
                title = "Quiz A",
                qandas = emptyList(),
            ),
            Quiz(
                id = UUID.randomUUID().toString(),
                title = "Quiz B",
                qandas = emptyList(),
            ),
        )

        val quizzes = repository.observeAll().first()
        quizzes shouldHaveSize 2
        // Vérifier les titres pour s'assurer que ce sont bien nos quiz
        quizzes.map { it.title }.shouldContainExactlyInAnyOrder("Quiz A", "Quiz B")
    }
}
