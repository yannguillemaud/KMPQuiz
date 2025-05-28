package ygmd.kmpquiz.domain.pojo

data class InternalQandaCategory(
    val id: Long? = null,
    val name: String,
    val descrption: String? = null,
    val iconUrl: String? = null,
    val questionCount: Int = 0,
    val downloadedCount: Int = 0,
)