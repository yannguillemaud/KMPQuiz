import org.jetbrains.annotations.TestOnly
import ygmd.kmpquiz.domain.scheduler.QuizScheduler

@TestOnly
class FakeAlarmScheduler : QuizScheduler {
    private val alarms = mutableMapOf<String, Long>()

    fun getAlarms(): Map<String, Long> = alarms
    fun isScheduled(id: String) = alarms.containsKey(id)

    override fun scheduleAlarm(quizId: String, exactTimestampEpochMillis: Long) {
        alarms[quizId] = exactTimestampEpochMillis
    }

    override fun cancelAlarm(quizId: String) {
        alarms.remove(quizId)
    }
}