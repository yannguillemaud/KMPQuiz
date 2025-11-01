package ygmd.kmpquiz.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import ygmd.kmpquiz.android.notification.NotificationUtils.requestPermissionNotification
import ygmd.kmpquiz.android.notification.NotificationUtils.setupNotificationChannel
import ygmd.kmpquiz.di.initKoin
import ygmd.kmpquiz.di.platformModule
import ygmd.kmpquiz.domain.usecase.notification.RescheduleTasksUseCase
import ygmd.kmpquiz.infra.appVersionProvider.AppVersionUpdateChecker
import ygmd.kmpquiz.navigation.AppNavigationState
import ygmd.kmpquiz.navigation.InitialNavigationEvent
import ygmd.kmpquiz.ui.App

class MainActivity : ComponentActivity() {
    private val applicationScope = CoroutineScope(Dispatchers.Main)
    private val showUpdateDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionNotification(this, this)
        setupNotificationChannel(this)
        initKoin {
            androidContext(this@MainActivity)
            modules(platformModule)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        scheduleInitialReminders()
//        checkForLatestRelease()
        handleStartingIntentIfExists(intent)
        setContent {
            if (showUpdateDialog.value) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = {
                        // Appelée si l'utilisateur clique en dehors de la dialog
                        showUpdateDialog.value = false
                    },
                    title = { androidx.compose.material3.Text("Update available") },
                    confirmButton = {
                        androidx.compose.material3.TextButton(
                            onClick = {
                                showUpdateDialog.value = false
                                val url = "https://github.com/yannguillemaud/KMPQuiz/releases"
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                startActivity(intent)
                            }
                        ) {
                            androidx.compose.material3.Text("Update")
                        }
                    },
                    dismissButton = {
                        androidx.compose.material3.TextButton(
                            onClick = { showUpdateDialog.value = false }
                        ) {
                            androidx.compose.material3.Text("Later")
                        }
                    }
                )
            }

            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleStartingIntentIfExists(intent)
    }

    private fun checkForLatestRelease() {
        val appVersionUpdateChecker by inject<AppVersionUpdateChecker>()
        lifecycleScope.launch {
            if (appVersionUpdateChecker.isUpdateAvailable()) {
                showUpdateDialog.value = true
            }
        }
    }

    private fun handleStartingIntentIfExists(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW && intent.data?.scheme == "myapp" && intent.data?.host == "quiz") {
            intent.data?.lastPathSegment?.let { quizId ->
                AppNavigationState.initialNavEvent.value = InitialNavigationEvent(quizId)
            }
        }
    }

    private fun scheduleInitialReminders() {
        val taskScheduler by inject<RescheduleTasksUseCase>()

        applicationScope.launch {
            try {
                taskScheduler.rescheduleAll()
            } catch (e: Exception) {}
        }
    }
}