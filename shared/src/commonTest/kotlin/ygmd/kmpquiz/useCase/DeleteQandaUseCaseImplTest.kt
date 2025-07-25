package ygmd.kmpquiz.useCase

import co.touchlab.kermit.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.createQanda
import ygmd.kmpquiz.domain.error.DomainError
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.DeleteQandasUseCaseImpl
import kotlin.test.Test
import kotlin.test.assertTrue

class DeleteQandaUseCaseImplTest {
    private val repository: QandaRepository = mockk()
    private val logger: Logger = mockk(relaxed = true)
    private val useCase: DeleteQandasUseCase = DeleteQandasUseCaseImpl(repository, logger)

    @Test
    fun `should delete qanda successfully`() = runTest {
        // Given
        val qanda = createQanda(id = 1)
        coEvery { repository.deleteById(any()) } returns Result.success(Unit)

        // When
        val result = useCase.delete(qanda)

        // Then
        assertTrue { result.isSuccess }
        coVerify { repository.deleteById(1) }
    }

    @Test
    fun `should return failure when repository throws error`() = runTest {
        // Given
        val qanda = createQanda(id = 1)
        coEvery { repository.deleteById(any()) } returns Result.failure(DomainError.QandaError.NotFound)

        // When
        val result = useCase.delete(qanda)

        // Then
        assertTrue { result.isFailure }
        coVerify { repository.deleteById(1) }
    }

    @Test
    fun `should return failure when delete qanda without id`() = runTest {
        // Given
        val qanda = createQanda(id = null)
        coEvery { repository.deleteById(any()) } returns Result.success(Unit)

        // When
        val result = useCase.delete(qanda)

        // Then
        assertTrue { result.isFailure }
        coVerify(exactly = 0) { repository.deleteById(any()) }
    }
}