package ygmd.kmpquiz

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.useCase.fetch.FetchQandasUseCase
import kotlin.test.Test

class FetchQandasUseCaseTest {
    private val fetchUseCase = mockk<FetchQandasUseCase>()

    @Test
    fun `should return error when fetch fail`(){
        // GIVEN
        coEvery { fetchUseCase() } returns Result.failure(IOException())
        val results = runBlocking { fetchUseCase.invoke() }
        assertThat(results.isFailure).isTrue
    }

    @Test
    fun `should return qandas when fetch success`(){
        // GIVEN
        val expectedQanda = QANDA("Science", "Question ?", listOf("Answers"), "CorrectAnswer")
        coEvery { fetchUseCase() } returns Result.success(listOf(expectedQanda))

        val results = runBlocking { fetchUseCase.invoke() }
        assertThat(results.isSuccess).isTrue
        assertThat(results.getOrThrow())
            .hasSize(1)
            .usingRecursiveComparison()
            .isEqualTo(expectedQanda)
    }
}