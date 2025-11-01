package ygmd.kmpquiz.data.repository.relation

import ygmd.kmpquiz.database.RelationEntity
import ygmd.kmpquiz.domain.dao.RelationDao
import ygmd.kmpquiz.domain.model.qanda.Qanda
import ygmd.kmpquiz.domain.repository.QandaRepository
import ygmd.kmpquiz.domain.repository.RelationRepository

class RelationRepositoryImpl(
    private val relationDao: RelationDao,
    private val qandaRepository: QandaRepository,
): RelationRepository {
    override suspend fun getQandasByQuizId(quizId: String): List<Qanda> {
        return relationDao.getRelationByQuizId(quizId)
            .map { qandaRepository.getById(it.qanda_id).getOrThrow() }
    }

    override suspend fun insertQandaToQuizId(
        quizId: String,
        qanda: Qanda
    ): Result<Unit> {
        return try {
            relationDao.insertRelation(
                RelationEntity(
                    quiz_id = quizId,
                    qanda_id = qanda.id,
                )
            )
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteQandaFromQuizId(
        quizId: String,
        qanda: Qanda
    ): Result<Unit> {
        return try {
            relationDao.deleteRelation(RelationEntity(
                quiz_id = quizId,
                qanda_id = qanda.id,
            ))
            Result.success(Unit)
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}