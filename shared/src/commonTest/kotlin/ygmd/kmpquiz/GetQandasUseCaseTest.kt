package ygmd.kmpquiz

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.assertj.core.api.Assertions
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.GetSavedQandaUseCase
import kotlin.test.Test

class GetQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val getQandasUseCase = GetSavedQandaUseCase(repository)

    @Test
    fun `should save qanda`(){
        // GIVEN
        val qanda = QANDA("Science", "Question ?", listOf("Answers"), "CorrectAnswer")
        every { repository.observeQandas() } returns flowOf(listOf(qanda))

        // WHEN
        val savedQandas = getQandasUseCase()

        // THEN
        verify { repository.observeQandas() }
        Assertions.assertThat(savedQandas).isEqualTo(qanda)
    }
}