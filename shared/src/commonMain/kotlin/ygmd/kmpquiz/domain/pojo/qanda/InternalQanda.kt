package ygmd.kmpquiz.domain.pojo.qanda

data class InternalQanda(
    val id: Long? = null,
    val category: String,
    val question: String,
    val answers: List<String>,
    val correctAnswer: String,
    val difficulty: String,
){
    val contentKey: String
        get() = "${question.trim().lowercase()}|${correctAnswer.trim().lowercase()}"
}

fun InternalQanda.toQandaContent(): QandaContent.TextualQanda {
    return QandaContent.TextualQanda(
        id = id,
        category = category,
        difficulty = difficulty,
        question = question,
        answers = answers,
        correctAnswer = correctAnswer
    )
}

// retrocompatibilité
fun QandaContent.toInternalQanda(): InternalQanda {
    return when(this){
        is QandaContent.TextualQanda ->
            InternalQanda(
                id = id,
                category = category,
                question = question,
                answers = answers,
                correctAnswer = correctAnswer,
                difficulty = difficulty
            )
        is QandaContent.ImageQanda ->
            InternalQanda(
                id = id,
                category = category,
                question = question,
                answers = answers,
                correctAnswer = correctAnswer,
                difficulty = difficulty
            )
    }
}