package ygmd.kmpquiz.data.repository.relation

import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.database.RelationEntity
import ygmd.kmpquiz.domain.dao.RelationDao

class PersistenceRelationDao(
    database: KMPQuizDatabase
): RelationDao {
    private val relationQueries = database.relationQueries

    override suspend fun getAllRelations(): List<RelationEntity> {
        return relationQueries.getAllRelations().executeAsList()
    }

    override suspend fun getRelationByQuizId(quizId: String): List<RelationEntity> {
        return relationQueries.getAllRelations()
            .executeAsList()
            .filter { it.quiz_id == quizId }
    }

    override suspend fun insertRelation(relation: RelationEntity) {
        relationQueries.insertRelation(
            quiz_id = relation.quiz_id,
            qanda_id = relation.qanda_id
        )
    }

    override suspend fun deleteRelation(relation: RelationEntity) {
        relationQueries.deleteQuizById(relation.quiz_id)
    }
}