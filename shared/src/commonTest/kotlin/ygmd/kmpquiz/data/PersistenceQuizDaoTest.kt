package ygmd.kmpquiz.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.data.database.DatabaseDriverFactory
import ygmd.kmpquiz.data.repository.quiz.PersistenceQuizDao
import ygmd.kmpquiz.database.KMPQuizDatabase
import ygmd.kmpquiz.domain.entities.qanda.Question
import ygmd.kmpquiz.domain.entities.quiz.DraftQuiz
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@OptIn(ExperimentalCoroutinesApi::class)
class PersistenceQuizDaoTest {
    private lateinit var driver: SqlDriver
    private lateinit var db: KMPQuizDatabase
    private lateinit var dao: PersistenceQuizDao

    @BeforeTest
    fun setup() {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also {
            KMPQuizDatabase.Schema.create(it)
        }
        db = KMPQuizDatabase(driver)
        dao = PersistenceQuizDao(object : DatabaseDriverFactory {
            override fun createDriver(): SqlDriver = driver
            override fun deleteDatabase() {
                /* no op */
            }
        })
    }

    @AfterTest
    fun tearDown() {
        driver.close()
    }


    @Test
    fun `emits empty list initially`() = runTest {
        dao.observeAllQuizzes().test {
            val initial = awaitItem()
            initial.size shouldBe 0
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits when inserting a quiz`() = runTest {
        dao.observeAllQuizzes().test {
            // 1. Empty initially
            awaitItem().size shouldBe 0

            // 2. Insert quiz
            db.quizQueries.insertQuiz(
                id = "1",
                title = "First Quiz",
                cron_expression = null,
                cron_display_name = null,
            )

            val afterInsert = awaitItem()
            afterInsert.size shouldBe 1
            afterInsert.first().title shouldBe "First Quiz"

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `emits when adding relation`() = runTest {
        dao.observeAllQuizzes().test {
            // 1. Empty initially
            awaitItem()

            // 2. Insert quiz
            db.quizQueries.insertQuiz(
                id = "1",
                title = "Quiz with Qanda",
                cron_expression = null,
                cron_display_name = null,
            )
            awaitItem() // quiz sans qandas

            // 3. Insert qanda
            db.qandaQueries.insertQanda(
                id = "100",
                context_key = "Question 1|Answer 1",
                question_type = "text",
                question_url = null,
                question_text = "Question 1",
                incorrect_answers_text = json.encodeToString(listOf("Answer 2", "Answer 3")),
                correct_answer_text = "Answer 1",
                category = "test",
            )

            // 4. Add relation
            db.quizQandasRelationQueries.insertRelation(quiz_id = "1", qanda_id = "100")

            // Nouvelle émission car relations observées aussi
            val afterRelation = awaitItem()
            val afterRelation2 = awaitItem()
            afterRelation2.size shouldBe 1
            afterRelation2.first().qandas.size shouldBe 1
            afterRelation2.first().qandas.first().question shouldBe Question.TextQuestion("Question 1")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `insertQuiz and get quiz by id - success`() = runTest {
        val draft = DraftQuiz(title = "Mon Quiz Inséré", qandas = emptyList())
        val result = dao.insertDraft(draft)
        result.shouldNotBeNull()
        val quizDb = dao.getQuizById(result)
        quizDb shouldNotBeNull {
            title shouldBe "Mon Quiz Inséré"
        }
    }

    // --- Tests pour getAllQuizzes ---
    @Test
    fun `getAllQuizzes - empty database returns empty list`() = runTest {
        val quizzes = dao.getAllQuizzes()
        quizzes.shouldBeEmpty()
    }

    @Test
    fun `getAllQuizzes - returns list with one quiz`() = runTest {
        val draft = DraftQuiz(title = "Mon Quiz Inséré", qandas = emptyList())
        val insertResult = dao.insertDraft(draft)
        insertResult.shouldNotBeNull()
        val quizzes = dao.getAllQuizzes()
        quizzes shouldHaveSize 1
        quizzes.first().id shouldBe insertResult
        quizzes.first().title shouldBe "Mon Quiz Inséré"
    }

    @Test
    fun `getAllQuizzes - returns list with multiple quizzes`() = runTest {
        val draft1 = DraftQuiz("Alpha Quiz", emptyList())
        val draft2 = DraftQuiz("Beta Quiz", emptyList())
        val insertedQuiz1 = dao.insertDraft(draft1)
        val insertedQuiz2 = dao.insertDraft(draft2)

        val quizzes = dao.getAllQuizzes()
        quizzes shouldHaveSize 2
        quizzes.map { it.id }.shouldContainExactlyInAnyOrder(insertedQuiz1, insertedQuiz2)
    }

    @Test
    fun `getQuizById - existing quiz returns the quiz`() = runTest {
        val draft = DraftQuiz("Mon Quiz Inséré", emptyList())
        val insertedQuiz = dao.insertDraft(draft)
        insertedQuiz.shouldNotBeNull()
        val foundQuiz = dao.getQuizById(insertedQuiz)
        foundQuiz shouldNotBeNull {
            id shouldBe insertedQuiz
            title shouldBe "Mon Quiz Inséré"
        }
    }

    @Test
    fun `getQuizById - non-existent quiz returns null`() = runTest {
        val foundQuiz = dao.getQuizById("id-inexistant-123")
        foundQuiz.shouldBeNull()
    }

    @Test
    fun `getQuizById - empty string ID returns null`() = runTest {
        val foundQuiz = dao.getQuizById("")
        foundQuiz.shouldBeNull()
    }

    @Test
    fun `deleteById on non existing quiz should throw`() = runTest {
        shouldThrow<IllegalStateException> {
            dao.deleteById("non existant id")
        }
    }

    @Test
    fun `deleteQuizById - removes all quizzes from database`() = runTest {
        val id = dao.insertDraft(DraftQuiz("Quiz à supprimer 2", emptyList()))
        id shouldNotBeNull {}
        dao.getAllQuizzes().size shouldBe 1
        dao.deleteById(id)
        val quizzesAfterDelete = dao.getAllQuizzes()
        quizzesAfterDelete.shouldBeEmpty()
    }
}
