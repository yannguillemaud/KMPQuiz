package ygmd.kmpquiz.db.exposed

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object QandasTable: LongIdTable() {
    val category = varchar("category", 255)
    val question = varchar("question", 255)
    val difficulty = varchar("difficulty", 255)
    /* TODO */
}

class QandaEntity(id: EntityID<Long>): LongEntity(id){
    companion object DAO: LongEntityClass<QandaEntity>(QandasTable)

    var category by QandasTable.category
    var question by QandasTable.question
    var difficulty by QandasTable.difficulty
}