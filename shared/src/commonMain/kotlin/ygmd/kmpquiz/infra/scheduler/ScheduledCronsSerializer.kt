package ygmd.kmpquiz.infra.scheduler

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ygmd.kmpquiz.domain.model.cron.ScheduledCrons
import java.io.InputStream
import java.io.OutputStream

object ScheduledCronsSerializer : Serializer<ScheduledCrons> {
    override val defaultValue: ScheduledCrons
        get() = ScheduledCrons()

    override suspend fun readFrom(input: InputStream): ScheduledCrons {
        return try {
            Json.decodeFromString<ScheduledCrons>(input.readBytes().decodeToString())
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read proto", e)
        }
    }

    override suspend fun writeTo(
        t: ScheduledCrons,
        output: OutputStream
    ) {
        output.write(
            Json.encodeToString(ScheduledCrons.serializer(), t)
                .encodeToByteArray()
        )
    }
}