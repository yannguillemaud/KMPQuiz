package ygmd.kmpquiz.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.createQanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.application.usecase.qanda.GetQandasUseCase
import ygmd.kmpquiz.application.usecase.qanda.GetQandaUseCase
import kotlin.Result.Companion.success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val getQandasUseCase: GetQandasUseCase = GetQandaUseCase(repository)

    @Test
    fun `should get qanda`() = runTest {
        // GIVEN
        val mockedQanda = createQanda()
        coEvery { repository.observeAll() } returns flowOf(listOf(mockedQanda))

        // WHEN
        val qandas = getQandasUseCase.observeAll().first()

        // THEN
        assertEquals(listOf(mockedQanda), qandas)
        coVerify { repository.observeAll() }
    }

    @Test
    fun `should get qanda for id`() = runTest {
        // GIVEN
        val mockedQanda = createQanda(id = 1)
        coEvery { repository.findById(any()) } returns success(mockedQanda)

        // WHEN
        val result = getQandasUseCase.getByid(1)

        // THEN
        assertTrue { result.isSuccess }
        assertEquals (mockedQanda, result.getOrThrow())
        coVerify { repository.findById(1) }
    }

    @Test
    fun `should get null if not exists`() = runTest {
        // GIVEN
        coEvery { repository.findById(any()) } returns Result.failure(RuntimeException())

        // WHEN
        val find = getQandasUseCase.getByid(1)

        // THEN
        assertTrue { find.isFailure }
        coVerify { repository.findById(1) }
    }
}