package ygmd.kmpquiz.domain.usecase.qanda

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.model.qanda.Answers
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.model.qanda.QuestionContent
import ygmd.kmpquiz.domain.repository.CategoryRepository
import ygmd.kmpquiz.domain.repository.QandaRepository
import java.util.UUID

class SaveQandaUseCase(
    private val qandaRepository: QandaRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke(draft: DraftQanda): Result<String> {
        val category = categoryRepository.getByName(draft.categoryName)
        if (category != null) return saveDraft(draft.question, draft.answers, category.id)
        return categoryRepository.addCategory(draft.categoryName)
            .onSuccess { saveDraft(draft.question, draft.answers, it) }
    }

    private suspend fun saveDraft(
        question: QuestionContent,
        answers: Answers,
        categoryId: String,
    ): Result<String> {
        val qanda = Qanda(
            id = UUID.randomUUID().toString(),
            question = question,
            answers = answers,
            categoryId = categoryId,
        )
        return qandaRepository.save(qanda).fold(
            onSuccess = { Result.success(qanda.id) },
            onFailure = { Result.failure(it) }
        )
    }
}