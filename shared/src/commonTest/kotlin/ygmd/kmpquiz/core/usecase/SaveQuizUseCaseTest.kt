package ygmd.kmpquiz.core.usecase

import FakeAlarmAlarmScheduler
import kotlinx.coroutines.test.runTest
import ygmd.kmpquiz.FakeSchedulerStore
import ygmd.kmpquiz.data.repository.FakeQuizRepository
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.quiz.config.QuizQuestionsConfiguration
import ygmd.kmpquiz.core.usecase.quiz.SaveQuizUseCase
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
        val scheduler = FakeAlarmAlarmScheduler()
        val useCase = SaveQuizUseCase(repository, store, scheduler)
        val quiz = Quiz(
            id = "1",
            title = "Fake Quiz",
            qandasConfiguration = QuizQuestionsConfiguration.AllQuestions()
        )

        // When
        val result = useCase.save(quiz)

        // Then
        assert(result.isFailure)
        assert(!scheduler.isScheduled(quiz.id))
        assertNull(store.getConfiguration(quiz.id))
    }
}


