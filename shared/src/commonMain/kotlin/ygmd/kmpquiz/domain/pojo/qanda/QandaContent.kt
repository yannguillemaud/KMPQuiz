package ygmd.kmpquiz.domain.pojo.qanda

sealed class QandaContent {
    abstract val id: Long?
    abstract val category: String
    abstract val difficulty: String
    abstract val contentKey: String

    data class TextualQanda(
        override val id: Long? = null,
        override val category: String,
        override val difficulty: String,
        val question: String,
        val answers: List<String>,
        val correctAnswer: String,
    ): QandaContent(){
        override val contentKey: String
            get() = "${question.trim().lowercase()}|${correctAnswer.trim().lowercase()}"
    }

    data class ImageQanda(
        override val id: Long? = null,
        override val category: String,
        override val difficulty: String,
        val question: String,
        val questionImageUrl: String? = null,
        val answers: List<String>,
        // answer, imaggeUrl
        val answersImage: Map<String, String> = emptyMap(),
        val correctAnswer: String,
    ): QandaContent(){
        override val contentKey: String
            get() = "${question.trim().lowercase()}|${correctAnswer.trim().lowercase()}"
    }
}