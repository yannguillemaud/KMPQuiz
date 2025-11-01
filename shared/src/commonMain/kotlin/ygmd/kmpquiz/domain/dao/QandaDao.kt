package ygmd.kmpquiz.domain.dao

import kotlinx.coroutines.flow.Flow
import ygmd.kmpquiz.database.QandaEntity

interface QandaDao {
    /**
     * Observe la liste complète des questions avec les détails de leur catégorie.
     * Retourne un Flow de QandaWithCategory.
     */
    fun observeQandasEntity(): Flow<List<QandaEntity>>

    /**
     * Récupère la liste complète des questions avec les détails de leur catégorie.
     * Retourne une List de QandaEntity.
     */
    suspend fun getAll(): List<QandaEntity>

    /**
     * Récupère une question par son ID, avec les détails de sa catégorie.
     */
    suspend fun getById(id: String): QandaEntity?

    /**
     * Récupère les questions d'une catégorie donnée, par son ID.
     */
    suspend fun getByCategory(categoryId: String): List<QandaEntity>

    /**
     * Récupère une question par sa clé de contexte.
     */
    suspend fun getByContextKey(contextKey: String): QandaEntity?

    /**
     * Sauvegarde une entité question. Note : cette méthode prend toujours un QandaEntity
     * car on insère dans la table de base, pas dans la vue.
     */
    suspend fun save(entity: QandaEntity): QandaEntity

    /**
     * Supprime toutes les questions.
     */
    suspend fun deleteAll()

    /**
     * Supprime une question par son ID.
     */
    suspend fun deleteById(id: String)

    suspend fun deleteByCategoryId(categoryId: String)
}