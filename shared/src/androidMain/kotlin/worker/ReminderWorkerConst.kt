package worker

object ReminderWorkerConst {
    const val CHANNEL_ID = "quiz_notifications"
}

enum class WorkRequestMetadataHeader(val value: String) {
    QUIZ_ID_KEY("quiz_id"),
}