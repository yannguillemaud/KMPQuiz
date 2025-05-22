package ygmd.kmpquiz.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.qualifier._q
import ygmd.kmpquiz.domain.pojo.QANDA

interface QandaRepository {
    fun observeQandas(): Flow<List<QANDA>>
    fun saveAll(qandas: List<QANDA>)
    fun save(qanda: QANDA)
    fun exists(qanda: QANDA): Boolean
}