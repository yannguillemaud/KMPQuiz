package ygmd.kmpquiz.useCase

import co.touchlab.kermit.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.entities.qanda.QuestionType.TextQuestion
import ygmd.kmpquiz.domain.entities.qanda.toQanda
import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.error.DomainError.QandaError.NotFound
import ygmd.kmpquiz.application.usecase.qanda.SaveQandasUseCaseImpl
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SaveQandasUseCaseTest {

    private val repository = mockk<QandaRepository>()
    private val logger = Logger.withTag("Test")
    private val useCase = SaveQandasUseCaseImpl(repository, logger)

    private val sampleQanda = InternalQanda(
        id = null,
        category = "Science",
        question = "What is H2O?",
        answers = listOf("Water", "Oxygen", "Hydrogen", "Air"),
        correctAnswer = "Water",
        difficulty = "easy"
    ).toQanda()

    @Test
    fun `should save qanda when it doesn't exist`() = runTest {
        // GIVEN
        coEvery { repository.findByContentKey(sampleQanda) } returns Result.failure(NotFound)
        coEvery { repository.save(sampleQanda) } returns Result.success(123L)

        // WHEN
        val result = useCase.save(sampleQanda)

        // THEN
        assertTrue(result.isSuccess)
        coVerify { repository.findByContentKey(sampleQanda) }
        coVerify { repository.save(sampleQanda) }
    }

    @Test
    fun `should fail when qanda already exists by content`() = runTest {
        // GIVEN
        val existingQanda = sampleQanda.copy(id = 456L)
        coEvery { repository.findByContentKey(sampleQanda) } returns Result.success(existingQanda)

        // WHEN
        val result = useCase.save(sampleQanda)

        // THEN
        assertTrue(result.isFailure)
        assertIs<DomainError.QandaError.AlreadyExists>(result.exceptionOrNull())
        coVerify { repository.findByContentKey(sampleQanda) }
        coVerify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should fail when qanda already exists by id`() = runTest {
        // GIVEN
        val qandaWithId = sampleQanda.copy(id = 123L)
        coEvery { repository.findById(123L) } returns Result.success(qandaWithId)

        // WHEN
        val result = useCase.save(qandaWithId)

        // THEN
        assertTrue { result.isFailure }
        assertIs<DomainError.QandaError.AlreadyExists>(result.exceptionOrNull())
        coVerify { repository.findById(123L) }
        coVerify(exactly = 0) { repository.save(any()) }
    }

    @Test
    fun `should save all unique qandas`() = runTest {
        // GIVEN
        val qandas = listOf(
            sampleQanda.copy(question = TextQuestion("Question 1")),
            sampleQanda.copy(question = TextQuestion("Question 2"))
        )

        qandas.forEach { qanda ->
            coEvery { repository.findByContentKey(qanda) } returns Result.failure(NotFound)
        }
        coEvery { repository.saveAll(qandas) } returns Result.success(Unit)

        // WHEN
        val result = useCase.saveAll(qandas)

        // THEN
        assertTrue(result.isSuccess)
        coVerify { repository.saveAll(qandas) }
    }

    @Test
    fun `should skip existing qandas in saveAll`() = runTest {
        // GIVEN
        val existingQanda = sampleQanda.copy(question = TextQuestion("Existing"))
        val newQanda = sampleQanda.copy(question = TextQuestion("New"))
        val qandas = listOf(existingQanda, newQanda)

        coEvery { repository.findByContentKey(existingQanda) } returns Result.success(existingQanda.copy(id = 1L))
        coEvery { repository.findByContentKey(newQanda) } returns Result.failure(NotFound)
        coEvery { repository.saveAll(listOf(newQanda)) } returns Result.success(Unit)

        // WHEN
        val result = useCase.saveAll(qandas)

        // THEN
        assertTrue(result.isSuccess)
        coVerify { repository.saveAll(listOf(newQanda)) }
    }

    @Test
    fun `should handle empty list in saveAll`() = runTest {
        // WHEN
        val result = useCase.saveAll(emptyList())

        // THEN
        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { repository.saveAll(any()) }
    }

    @Test
    fun `should remove duplicates from input in saveAll`() = runTest {
        // GIVEN
        val duplicate1 = sampleQanda.copy(question = TextQuestion("Same"))
        val duplicate2 = sampleQanda.copy(question = TextQuestion("Same")) // Même contentKey
        val qandas = listOf(duplicate1, duplicate2)

        coEvery { repository.findByContentKey(duplicate1) } returns Result.failure(NotFound)
        coEvery { repository.saveAll(listOf(duplicate1)) } returns Result.success(Unit)

        // WHEN
        val result = useCase.saveAll(qandas)

        // THEN
        assertTrue(result.isSuccess)
        coVerify { repository.saveAll(listOf(duplicate1)) } // Un seul élément sauvé
    }
}