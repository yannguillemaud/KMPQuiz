package ygmd.kmpquiz.core.usecase.qanda

import kotlinx.coroutines.flow.collectLatest
import ygmd.kmpquiz.core.domain.qanda.Qanda
import ygmd.kmpquiz.core.domain.qanda.QandaDetails
import ygmd.kmpquiz.core.repository.CategoryRepository
import ygmd.kmpquiz.core.repository.FetcherRepository
import ygmd.kmpquiz.core.repository.QandaRepository
import java.util.UUID

class DownloadQandasUseCase(
    private val fetcherRepository: FetcherRepository,
    private val qandaRepository: QandaRepository,
    private val categoryRepository: CategoryRepository,
) {
    suspend operator fun invoke() {
        fetcherRepository.observeFetchers().collectLatest {
            it.forEach { fetcher ->
                fetcherRepository.startDownload(fetcher.id).fold(
                    onSuccess = { qandas -> saveFetchedQandas(qandas) },
                    onFailure = {}
                )
            }
        }
    }

    suspend operator fun invoke(fetcherId: String): Result<Unit> {
        val fetchResult = fetcherRepository.startDownload(fetcherId)
        return fetchResult.fold(onSuccess = {
            saveFetchedQandas(it)
            Result.success(Unit)
        }, onFailure = {
            Result.failure(it)
        })
    }

    private suspend fun saveFetchedQandas(fetchedQandas: List<QandaDetails>) {
        val newQandas = fetchedQandas.filter {
            qandaRepository.existsByContextKey(it.contextKey).not()
        }
        newQandas.forEach {
            val category = categoryRepository.getByName(it.categoryName)
            val categoryId = if (category != null) category.id
            else {
                val categoryId = UUID.randomUUID().toString()
                categoryRepository.saveCategory(categoryId, it.categoryName)
                categoryId
            }
            qandaRepository.save(
                Qanda(
                    id = UUID.randomUUID().toString(),
                    categoryId = categoryId,
                    question = it.question,
                    answers = it.answers,
                )
            )
        }
    }
}