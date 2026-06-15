import org.jetbrains.annotations.TestOnly
import ygmd.kmpquiz.core.service.scheduler.QuizAlarmScheduler

@TestOnly
class FakeAlarmAlarmScheduler : QuizAlarmScheduler {
    private val alarms = mutableMapOf<String, Long>()

    fun getAlarms(): Map<String, Long> = alarms
    fun isScheduled(id: String) = alarms.containsKey(id)

    override fun scheduleAlarm(quizId: String, exactEpochMillis: Long) {
        alarms[quizId] = exactEpochMillis
    }

    override fun cancelAlarm(quizId: String) {
        alarms.remove(quizId)
    }
}