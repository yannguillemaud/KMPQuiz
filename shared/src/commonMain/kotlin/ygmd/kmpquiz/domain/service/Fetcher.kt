package ygmd.kmpquiz.domain.service

import ygmd.kmpquiz.domain.model.draftqanda.DraftQanda
import ygmd.kmpquiz.domain.result.FetchResult

interface Fetcher {
    val id: String
    val name: String
    suspend fun fetch(): FetchResult<List<DraftQanda>>
}