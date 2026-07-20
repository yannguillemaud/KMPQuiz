package ygmd.kmpquiz.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.brewkits.grant.GrantLauncher
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.impl.AndroidGrantLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject
import ygmd.kmpquiz.presentation.App
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val pendingQuizTarget = MutableStateFlow<String?>(null)
    private val grantManager: GrantManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingQuizTarget.value = extractQuizId(intent)
        enableEdgeToEdge()
        grantManager.setLauncher(AndroidGrantLauncher.from(this))
        setContent {
            val quizId by pendingQuizTarget.collectAsState()
            App(
                quizId = quizId,
                onDeepLinkConsumed = {
                    pendingQuizTarget.value = null
                }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val newQuizId = extractQuizId(intent)
        if (newQuizId != null) {
            pendingQuizTarget.value = newQuizId
        }
    }

    private fun extractQuizId(intent: Intent?): String? {
        val data = intent?.data ?: return null
        return if (intent.action == Intent.ACTION_VIEW && data.scheme == "myapp" && data.host == "quiz") {
            data.lastPathSegment
        } else null
    }
}