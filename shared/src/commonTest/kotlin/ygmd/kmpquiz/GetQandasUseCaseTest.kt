package ygmd.kmpquiz

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.SavedQandaRepository
import kotlin.test.Test

class GetQandasUseCaseTest {
    private val repository = mockk<SavedQandaRepository>()
    private val getQandasUseCase = GetQandaUseCase(repository)

    @Test
    fun `should save qanda`() = runTest {
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            categoryId = 1,
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswerPosition = 3,
        )
        coEvery { repository.getQandas() } returns flowOf(listOf(qanda))

        // WHEN
        val savedQandas = getQandasUseCase.getAll().first()

        // THEN
        coVerify { repository.getQandas() }
        Assertions.assertThat(savedQandas).isEqualTo(listOf(qanda))
    }

    @Test
    fun `should get qanda if exists`() = runTest {
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            categoryId = 1,
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswerPosition = 1,
        )
        coEvery { repository.findById(any()) } returns qanda

        // WHEN
        val find = getQandasUseCase.find(1)

        // THEN
        coVerify { repository.findById(1) }
        Assertions.assertThat(find).isEqualTo(qanda)
    }

    @Test
    fun `should get null if not exists`() = runTest {
        // GIVEN
        coEvery { repository.findById(any()) } returns null

        // WHEN
        val find = getQandasUseCase.find(1)

        // THEN
        coVerify { repository.findById(1) }
        Assertions.assertThat(find).isNull()
    }
}