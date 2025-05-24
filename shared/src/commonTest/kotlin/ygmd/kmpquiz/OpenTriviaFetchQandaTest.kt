package ygmd.kmpquiz

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException
import org.assertj.core.api.Assertions.assertThat
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.useCase.fetch.OpenTriviaFetchQanda
import kotlin.test.Test

class OpenTriviaFetchQandaTest {
    private val fetchUseCase = mockk<OpenTriviaFetchQanda>()

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
        val expectedQanda = InternalQanda(
            id = 1,
            category = "Science",
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswer = "CorrectAnswer"
        )
        coEvery { fetchUseCase() } returns Result.success(listOf(expectedQanda))

        val results = runBlocking { fetchUseCase.invoke() }
        assertThat(results.isSuccess).isTrue
        assertThat(results.getOrThrow())
            .hasSize(1)
            .isEqualTo(listOf(expectedQanda))
    }
}