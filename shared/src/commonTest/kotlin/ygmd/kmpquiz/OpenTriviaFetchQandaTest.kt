package ygmd.kmpquiz

/*
class OpenTriviaFetchQandaTest {

    private fun createMockClient(
        status: HttpStatusCode = HttpStatusCode.OK,
        content: String = "",
        headers: Map<String, String> = emptyMap()
    ): HttpClient {
        val mockEngine = MockEngine { _ ->
            respond(
                content = content,
                status = status,
                headers = buildHeaders {
                    headers.forEach { (key, value) ->
                        append(key, value)
                    }
                    if (!headers.containsKey(HttpHeaders.ContentType)) {
                        append(HttpHeaders.ContentType, "application/json")
                    }
                }
            )
        }

        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    private fun createFetchUseCase(client: HttpClient): OpenTriviaFetcher {
        return OpenTriviaFetcher(
            client = client,
            logger = Logger.withTag("Test")
        )
    }

    @Test
    fun `should return success with qandas when API returns valid data`() = runTest {
        // GIVEN
        val client = createMockClient(content = VALID_API_RESPONSE)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Success<List<Qanda>>>(result)
        assertEquals(2, result.data.size)

        val firstQanda = result.data[0]
        assertEquals("Politics", firstQanda.category)
        assertEquals(
            "Which US state was the first to allow women to vote in 1869?",
            firstQanda.question
        )
        assertEquals("Wyoming".lowercase(), firstQanda.correctAnswer.contextKey)
        assertEquals("hard", firstQanda.difficulty)
        assertEquals(4, firstQanda.answers.size)
        assertTrue(firstQanda.answers.contains("Wyoming"))
    }

    @Test
    fun `should return success with empty list when API returns no results`() = runTest {
        // GIVEN
        val client = createMockClient(content = NO_RESULTS_API_RESPONSE)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Success<List<InternalQanda>>>(result)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `should return rate limit failure when API returns 429`() = runTest {
        // GIVEN
        val client = createMockClient(
            status = HttpStatusCode.TooManyRequests,
            headers = mapOf("Retry-After" to "30")
        )
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.RATE_LIMIT, result.type)
        assertTrue(result.message.contains("Trop de requêtes"))
        assertEquals(30.seconds, result.retryAfter)
    }

    @Test
    fun `should return rate limit failure without retry time when header is missing`() = runTest {
        // GIVEN
        val client = createMockClient(status = HttpStatusCode.TooManyRequests)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.RATE_LIMIT, result.type)
        assertNull(result.retryAfter)
    }

    @Test
    fun `should return API error when server returns 4xx error`() = runTest {
        // GIVEN
        val client = createMockClient(status = HttpStatusCode.BadRequest)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("400"))
    }

    @Test
    fun `should return API error when server returns 5xx error`() = runTest {
        // GIVEN
        val client = createMockClient(status = HttpStatusCode.InternalServerError)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("500"))
    }

    @Test
    fun `should return API error when response body is empty`() = runTest {
        // GIVEN
        val client = createMockClient(content = "")
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("Réponse vide"))
    }

    @Test
    fun `should return network error when JSON parsing fails`() = runTest {
        // GIVEN
        val client = createMockClient(content = "Invalid JSON")
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.NETWORK_ERROR, result.type)
        assertTrue(result.message.contains("parsing"))
        assertNotNull(result.cause)
    }

    @Test
    fun `should return API error for invalid parameter response code`() = runTest {
        // GIVEN
        val client = createMockClient(content = createApiResponseWithCode(2))
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("Paramètres invalides"))
    }

    @Test
    fun `should return API error for token not found response code`() = runTest {
        // GIVEN
        val client = createMockClient(content = createApiResponseWithCode(3))
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("Token non trouvé"))
    }

    @Test
    fun `should return API error for empty token response code`() = runTest {
        // GIVEN
        val client = createMockClient(content = createApiResponseWithCode(4))
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("Token vide"))
    }

    @Test
    fun `should return API error for unknown response code`() = runTest {
        // GIVEN
        val client = createMockClient(content = createApiResponseWithCode(99))
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.API_ERROR, result.type)
        assertTrue(result.message.contains("Erreur API inconnue: 99"))
    }

    @Test
    fun `should handle invalid retry-after header gracefully`() = runTest {
        // GIVEN
        val client = createMockClient(
            status = HttpStatusCode.TooManyRequests,
            headers = mapOf("Retry-After" to "not-a-number")
        )
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.RATE_LIMIT, result.type)
        assertNull(result.retryAfter)
    }

    @Test
    fun `should handle negative retry-after header gracefully`() = runTest {
        // GIVEN
        val client = createMockClient(
            status = HttpStatusCode.TooManyRequests,
            headers = mapOf("Retry-After" to "-10")
        )
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.RATE_LIMIT, result.type)
        assertNull(result.retryAfter)
    }

    @Test
    fun `should handle zero retry-after header gracefully`() = runTest {
        // GIVEN
        val client = createMockClient(
            status = HttpStatusCode.TooManyRequests,
            headers = mapOf("Retry-After" to "0")
        )
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Failure>(result)
        assertEquals(FailureType.RATE_LIMIT, result.type)
        assertNull(result.retryAfter)
    }

    @Test
    fun `should preserve correct answer in mixed question types`() = runTest {
        // GIVEN
        val client = createMockClient(content = MIXED_QUESTION_TYPES_RESPONSE)
        val useCase = createFetchUseCase(client)

        // WHEN
        val result = useCase.fetch()

        // THEN
        assertIs<FetchResult.Success<List<Qanda>>>(result)
        assertEquals(2, result.data.size)

        // Multiple choice question
        val multipleChoice = result.data[0]
        assertEquals("Wyoming", (multipleChoice.correctAnswer as AnswerContent.TextAnswer).text)
        assertTrue(multipleChoice.answers.contains("Wyoming"))
        assertEquals(4, multipleChoice.answers.size)

        // Boolean question
        val booleanQuestion = result.data[1]
        assertEquals("True", (booleanQuestion.correctAnswer as AnswerContent.TextAnswer).text)
        assertTrue(booleanQuestion.answers.contains("True"))
        assertTrue(booleanQuestion.answers.contains("False"))
        assertEquals(2, booleanQuestion.answers.size)
    }

    // Helper pour créer des réponses API avec différents codes
    private fun createApiResponseWithCode(responseCode: Int): String {
        return """
        {
            "response_code": $responseCode,
            "results": []
        }
        """.trimIndent()
    }

    companion object {
        private val VALID_API_RESPONSE = """
        {
            "response_code": 0,
            "results": [
                {
                    "type": "multiple",
                    "difficulty": "hard",
                    "category": "Politics",
                    "question": "Which US state was the first to allow women to vote in 1869?",
                    "correct_answer": "Wyoming",
                    "incorrect_answers": ["California", "Delaware", "Virginia"]
                },
                {
                    "type": "boolean",
                    "difficulty": "easy",
                    "category": "Science",
                    "question": "The human heart has four chambers.",
                    "correct_answer": "True",
                    "incorrect_answers": ["False"]
                }
            ]
        }
        """.trimIndent()

        private val NO_RESULTS_API_RESPONSE = """
        {
            "response_code": 1,
            "results": []
        }
        """.trimIndent()

        private val MIXED_QUESTION_TYPES_RESPONSE = """
        {
            "response_code": 0,
            "results": [
                {
                    "type": "multiple",
                    "difficulty": "hard",
                    "category": "Politics",
                    "question": "Which US state was the first to allow women to vote in 1869?",
                    "correct_answer": "Wyoming",
                    "incorrect_answers": ["California", "Delaware", "Virginia"]
                },
                {
                    "type": "boolean",
                    "difficulty": "easy",
                    "category": "Science",
                    "question": "The human heart has four chambers.",
                    "correct_answer": "True",
                    "incorrect_answers": ["False"]
                }
            ]
        }
        """.trimIndent()
    }
}
*/