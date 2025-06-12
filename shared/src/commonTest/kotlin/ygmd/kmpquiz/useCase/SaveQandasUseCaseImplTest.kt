package ygmd.kmpquiz.useCase

import co.touchlab.kermit.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.domain.error.DomainError.QandaError.AlreadyExists
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCase
import ygmd.kmpquiz.domain.usecase.SaveQandasUseCaseImpl
import kotlin.test.Test

class SaveQandasUseCaseImplTest {
    private val repository: QandaRepository = mockk<QandaRepository>()
    private val logger: Logger = mockk(relaxed = true)
    private val saveUseCase: SaveQandasUseCase = SaveQandasUseCaseImpl(repository, logger)

    @Test
    fun `should save qanda`() = runTest {
        // GIVEN
        val mockedQanda = createInternalQanda()

        coEvery { repository.save(any()) } returns Result.success(1)

        // WHEN
        saveUseCase.save(mockedQanda)

        // THEN
        coVerify { repository.save(mockedQanda) }
    }

    @Test
    fun `should not save qanda when already exists by id`() = runTest {
        // GIVEN
        val mockedQanda = createInternalQanda(id = 1)
        val mockedQanda2 = createInternalQanda(id = 1)

        coEvery { repository.save(mockedQanda2) } returns Result.failure(AlreadyExists)

        // WHEN
        saveUseCase.save(mockedQanda)

        val result = saveUseCase.save(mockedQanda2)

        // THEN
        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull())
            .isNotNull
            .isInstanceOf(AlreadyExists::class.java)

        coVerify {
            repository.save(mockedQanda)
            repository.save(mockedQanda2)
        }
    }

    @Test
    fun `should not save qanda when already exists by content key`() = runTest {
        // GIVEN
        val mockedQanda = createInternalQanda(id = 1)
        val mockedQanda2 = createInternalQanda(id = null)

        coEvery { repository.save(mockedQanda2) } returns Result.failure(AlreadyExists)

        // WHEN
        saveUseCase.save(mockedQanda)

        val result = saveUseCase.save(mockedQanda2)

        // THEN
        assertThat(result.isFailure).isTrue
        assertThat(result.exceptionOrNull())
            .isNotNull
            .isInstanceOf(AlreadyExists::class.java)

        coVerify {
            repository.save(mockedQanda)
            repository.save(mockedQanda2)
        }
    }
}