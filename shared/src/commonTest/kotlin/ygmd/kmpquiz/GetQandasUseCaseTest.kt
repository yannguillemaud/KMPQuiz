package ygmd.kmpquiz

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.save.GetSavedQandaUseCase
import kotlin.test.Test

class GetQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val getQandasUseCase = GetSavedQandaUseCase(repository)

    @Test
    fun `should save qanda`() = runTest {
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            category = "Science",
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswer = "CorrectAnswer"
        )
        every { repository.observeQandas() } returns flowOf(listOf(qanda))

        // WHEN
        val savedQandas = getQandasUseCase.getAll().first()

        // THEN
        verify { repository.observeQandas() }
        Assertions.assertThat(savedQandas).isEqualTo(listOf(qanda))
    }

    @Test
    fun `should get qanda if exists`(){
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            category = "Science",
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswer = "CorrectAnswer"
        )
        every { repository.findById(any()) } returns qanda

        // WHEN
        val find = getQandasUseCase.find(1)

        // THEN
        verify { repository.findById(1) }
        Assertions.assertThat(find).isEqualTo(qanda)
    }

    @Test
    fun `should get null if not exists`(){
        // GIVEN
        every { repository.findById(any()) } returns null

        // WHEN
        val find = getQandasUseCase.find(1)

        // THEN
        verify { repository.findById(1) }
        Assertions.assertThat(find).isNull()
    }
}