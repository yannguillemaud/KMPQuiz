package ygmd.kmpquiz.data.result

import ygmd.kmpquiz.database.QandaEntity

data class SaveAllResult(
    val inserted: List<QandaEntity>,
    val existing: List<QandaEntity>
) {
    val hasConflitcs: Boolean get() = existing.isNotEmpty()
}