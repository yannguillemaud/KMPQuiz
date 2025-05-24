package ygmd.kmpquiz.db.exposed

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object QandasTable: LongIdTable() {
    val category = varchar("category", 255)
    val question = varchar("question", 255)
    val answers = varchar("answers", length = 255 * 4)
    val correctPosition = integer("correct_answer_position")
}

class QandaEntity(id: EntityID<Long>): LongEntity(id){
    companion object DAO: LongEntityClass<QandaEntity>(QandasTable)

    var category by QandasTable.category
    var question by QandasTable.question
    var answers by QandasTable.answers
    var correctPosition by QandasTable.correctPosition
}