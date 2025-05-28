package ygmd.kmpquiz

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.SavedQandaRepository
import ygmd.kmpquiz.domain.useCase.save.SaveQandaUseCase
import kotlin.test.Test

class SaveQandasUseCaseTest {
    private val repository = mockk<SavedQandaRepository>()
    private val saveUseCase = SaveQandaUseCase(repository)

    @Test
    fun `should save qanda`() = runTest {
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            categoryId = 1,
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswerPosition = 1,
        )
        coEvery { saveUseCase.saveQanda(any()) } returns Unit

        // WHEN
        saveUseCase.saveQanda(qanda)

        // THEN
        coVerify { repository.saveQanda(qanda) }
    }
}