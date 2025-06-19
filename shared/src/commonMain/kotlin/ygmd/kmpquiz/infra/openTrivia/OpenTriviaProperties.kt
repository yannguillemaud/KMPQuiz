package ygmd.kmpquiz.infra.openTrivia

const val URL = "https://opentdb.com/api.php"

object OpenTriviaDefaults {
    fun defaultBuilder(): OpenTriviaUrlBuilder = OpenTriviaUrlBuilder()
    fun bulkBuilder(amount: Int = 50): OpenTriviaUrlBuilder = OpenTriviaUrlBuilder().withAmount(amount)
}

class OpenTriviaUrlBuilder(
) {
    private var amount: Int = 1
        set(value) {
            require(value in 1..50) { "Amount must be between 1 and 50"}
            field = value
        }
    private var category: String? = null
    private var difficulty: String? = null
    private var type: OpenTriviaType? = null

    fun withAmount(selectedAmount: Int): OpenTriviaUrlBuilder {
        apply { amount = selectedAmount }
        return this
    }

    fun withCategory(selectedCategory: String?): OpenTriviaUrlBuilder {
        apply { category = selectedCategory }
        return this
    }

    fun withDifficulty(selectedDifficulty: String?): OpenTriviaUrlBuilder {
        apply { difficulty = selectedDifficulty }
        return this
    }

    fun withType(selectedType: OpenTriviaType): OpenTriviaUrlBuilder {
        apply { type = selectedType }
        return this
    }

    fun build(): String {
        val queryString = buildQueryString()
        return "$URL?$queryString"
    }

    private fun buildQueryString(): String = buildParameters().joinToString("&")

    private fun buildParameters(): List<String> = buildList {
        add("amount=$amount")
        category?.let { add("category=$it") }
        difficulty?.let { add("difficulty=$it") }
        type?.let { add("type=${it.value}") }
    }
}

enum class OpenTriviaType(val value: String) {
    BOOLEAN("boolean"), MULTIPLE("multiple")
}