package ygmd.kmpquiz.domain.entities.qanda

fun Qanda.toInternalQanda(): InternalQanda =
    InternalQanda(
        id = id,
        category = category,
        question = with(qandaQuestion) {
            when (this) {
                is QuestionType.TextQuestion -> text
                is QuestionType.ImageQuestion -> image.url
            }
        },
        answers = answers.map { it.contextKey },
        correctAnswer = correctAnswer.contextKey,
        difficulty = difficulty
    )

fun InternalQanda.toQanda(): Qanda {
    val textAnswers = answers.toTextAnswers(correctAnswer)
    return Qanda(
        id = id,
        category = category,
        difficulty = difficulty,
        qandaQuestion = QuestionType.TextQuestion(question),
        answers = textAnswers
    )
}

fun List<String>.toTextAnswers(correctAnswer: String) =
    map { AnswerContent.TextAnswer(it, it == correctAnswer) }