package ygmd.kmpquiz.domain.dao

import ygmd.kmpquiz.database.RelationEntity

interface RelationDao {
    suspend fun getAllRelations(): List<RelationEntity>
    suspend fun getRelationByQuizId(quizId: String): List<RelationEntity>
    suspend fun insertRelation(relation: RelationEntity)
    suspend fun deleteRelation(relation: RelationEntity)
}