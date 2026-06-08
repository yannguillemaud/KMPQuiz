package ygmd.kmpquiz.domain.usecase

import FakeAlarmScheduler
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.FakeSchedulerStore
import ygmd.kmpquiz.data.repository.FakeQuizRepository
import ygmd.kmpquiz.domain.model.quiz.Quiz
import ygmd.kmpquiz.domain.model.quiz.QuizConfigDetails
import ygmd.kmpquiz.domain.usecase.quiz.SaveQuizUseCase
import kotlin.test.Test
import kotlin.test.assertNull

class SaveQuizUseCaseTest {

    @Test
    fun `should return failure when save fails`() = runTest {
        // Given
        val repository = FakeQuizRepository().apply {
            shouldSaveFail = true
        }
        val store = FakeSchedulerStore()
        val scheduler = FakeAlarmScheduler()
        val useCase = SaveQuizUseCase(repository, store, scheduler)
        val quiz = Quiz(
            id = "1",
            title = "Fake Quiz",
            qandasConfiguration = QuizConfigDetails.AllQuestions()
        )

        // When
        val result = useCase.save(quiz)

        // Then
        assert(result.isFailure)
        assert(!scheduler.isScheduled(quiz.id))
        assertNull(store.getConfiguration(quiz.id))
    }
}


