package ygmd.kmpquiz.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.createQanda
import ygmd.kmpquiz.data.repository.qanda.QandaRepository
import ygmd.kmpquiz.domain.usecase.GetQandasUseCase
import ygmd.kmpquiz.domain.usecase.GetQandasUseCaseImpl
import kotlin.Result.Companion.success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val getQandasUseCase: GetQandasUseCase = GetQandasUseCaseImpl(repository)

    @Test
    fun `should get qanda`() = runTest {
        // GIVEN
        val mockedQanda = createQanda()
        coEvery { repository.getAll() } returns flowOf(listOf(mockedQanda))

        // WHEN
        val qandas = getQandasUseCase.execute().first()

        // THEN
        assertEquals(listOf(mockedQanda), qandas)
        coVerify { repository.getAll() }
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