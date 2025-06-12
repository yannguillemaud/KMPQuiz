package ygmd.kmpquiz.useCase

import co.touchlab.kermit.Logger
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCase
import ygmd.kmpquiz.domain.usecase.DeleteQandasUseCaseImpl
import kotlin.test.Test

class DeleteQandaUseCaseImplTest {
    private val repository: QandaRepository = mockk()
    private val logger: Logger = mockk()
    private val useCase: DeleteQandasUseCase = DeleteQandasUseCaseImpl(repository, logger)

    @Test
    fun `should delete qanda successfully`() = runTest {
        // Given
        val qanda = createInternalQanda(id = 1)
        coEvery { repository.deleteById(any()) } returns Result.success(Unit)

        // When
        val result = useCase.delete(qanda)

        // Then
        assertThat(result.isSuccess).isTrue
    }
}