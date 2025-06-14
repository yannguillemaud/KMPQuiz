package ygmd.kmpquiz.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import ygmd.kmpquiz.db.exposed.QandaEntity
import ygmd.kmpquiz.db.exposed.QandaEntity.DAO.all
import ygmd.kmpquiz.db.exposed.QandasTable
import ygmd.kmpquiz.domain.pojo.qanda.InternalQanda
import ygmd.kmpquiz.domain.repository.SavedQandaRepository

class QandaRepositoryPersistenceImpl: SavedQandaRepository {
    override fun observeQandas(): Flow<List<InternalQanda>> = flow {
        val qandas = withContext(Dispatchers.IO){
            transaction {
                all().toList().map { it.toInternalQanda() }
            }
        }
        emit(qandas)
    }

    override suspend fun getQandas(): List<InternalQanda> {
        return withContext(Dispatchers.IO){
            transaction {
                all().map { it.toInternalQanda() }
            }
        }
    }

    override suspend fun findById(id: Long): InternalQanda? =
        withContext(Dispatchers.IO) {
            transaction {
                QandaEntity.findById(id)?.toInternalQanda()
            }
        }

    override suspend fun saveQandas(qandas: List<InternalQanda>): Unit =
        withContext(Dispatchers.IO){

        }

    override suspend fun saveQanda(qanda: InternalQanda) {
        withContext(Dispatchers.IO){
            transaction {
                QandaEntity.new {
                    category = qanda.category
                    question = qanda.question
                    answers = qanda.answers.joinToString(",")
                    correctPosition = qanda.answers.indexOf(qanda.correctAnswer)
                }
            }
        }
    }

    override suspend fun deleteById(id: Long): Boolean {
        return withContext(Dispatchers.IO){
            transaction {
                val entity = QandaEntity.findById(id)
                if(entity != null){
                    entity.delete()
                    true
                } else false
            }
        }
    }

    override suspend fun deleteAll(id: Long): Boolean {
        return withContext(Dispatchers.IO){
            transaction {
                QandasTable.deleteAll() == 0
            }
        }
    }
}

private fun QandaEntity.toInternalQanda(): InternalQanda =
    InternalQanda(
        id = id.value,
        category = category,
        question = question,
        answers = answers.split(",").toList(),
        correctAnswer = answers
    )
