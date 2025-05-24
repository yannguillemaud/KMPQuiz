package ygmd.kmpquiz

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.save.SaveQandaUseCase
import kotlin.test.Test

class SaveQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val saveUseCase = SaveQandaUseCase(repository)

    @Test
    fun `should save qanda`(){
        // GIVEN
        val qanda = InternalQanda(
            id = 1,
            category = "Science",
            question = "Question ?",
            answers = listOf("Answers"),
            correctAnswer = "CorrectAnswer"
        )
        every { saveUseCase.saveQanda(any()) } returns Unit

        // WHEN
        saveUseCase.saveQanda(qanda)

        // THEN
        verify { repository.save(qanda) }
    }
}