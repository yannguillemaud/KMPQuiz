package ygmd.kmpquiz.domain.fetch

import ygmd.kmpquiz.domain.pojo.InternalQanda
import java.time.Duration

sealed class FetchResult<out T>{
    data class Success<T>(val data: T): FetchResult<T>()
    data class RateLimit(val retryAfter: Duration? = null): FetchResult<Nothing>()
    data class ApiError(val code: Int, val message: String): FetchResult<Nothing>()
    data class Error(val throwable: Throwable): FetchResult<Nothing>()
}

interface FetchQandaService {
    suspend fun fetch(): FetchResult<List<InternalQanda>>
}