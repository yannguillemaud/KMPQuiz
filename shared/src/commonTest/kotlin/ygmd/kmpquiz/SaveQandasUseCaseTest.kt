package ygmd.kmpquiz

import io.mockk.mockk
import io.mockk.verify
import ygmd.kmpquiz.domain.pojo.QANDA
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.useCase.SaveQandaUseCase
import kotlin.test.Test

class SaveQandasUseCaseTest {
    private val repository = mockk<QandaRepository>()
    private val saveUseCase = SaveQandaUseCase(repository)

    @Test
    fun `should save qanda`(){
        // GIVEN
        val qanda = QANDA("Science", "Question ?", listOf("Answers"), "CorrectAnswer")

        // WHEN
        saveUseCase.saveQanda(qanda)

        // THEN
        verify { repository.save(qanda) }
    }
}