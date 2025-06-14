package ygmd.kmpquiz.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.createInternalQanda
import ygmd.kmpquiz.domain.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCaseImpl
import kotlin.Result.Companion.success
import kotlin.test.Test

class GetQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val getQandasUseCase: GetQandasUseCase = GetQandasUseCaseImpl(repository)

    @Test
    fun `should get qanda`() = runTest {
        // GIVEN
        val mockedQanda = createInternalQanda()
        coEvery { repository.getAll() } returns flowOf(listOf(mockedQanda))

        // WHEN
        val qanda = getQandasUseCase.execute().first()

        // THEN
        assertThat(qanda)
            .hasSize(1)
            .isEqualTo(listOf(mockedQanda))
        coVerify { repository.getAll() }
    }

    @Test
    fun `should get qanda for id`() = runTest {
        // GIVEN
        val mockedQanda = createInternalQanda(id = 1)
        coEvery { repository.findById(any()) } returns success(mockedQanda)

        // WHEN
        val qanda = getQandasUseCase.getByid(1)

        // THEN
        assertThat(qanda.isSuccess).isTrue
        assertThat (qanda.getOrThrow())
            .isEqualTo(mockedQanda)
        coVerify { repository.findById(1) }
    }

    @Test
    fun `should get null if not exists`() = runTest {
        // GIVEN
        coEvery { repository.findById(any()) } returns Result.failure(RuntimeException())

        // WHEN
        val find = getQandasUseCase.getByid(1)

        // THEN
        assertThat(find.isFailure).isTrue
        coVerify { repository.findById(1) }
    }
}