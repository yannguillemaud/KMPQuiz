package ygmd.kmpquiz.android

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import ygmd.kmpquiz.android.notification.NotificationUtils.requestPermissionNotification
import ygmd.kmpquiz.android.notification.NotificationUtils.setupNotificationChannel
import ygmd.kmpquiz.android.ui.views.fetch.success.FetchScreen
import ygmd.kmpquiz.android.ui.views.home.HomeScreen
import ygmd.kmpquiz.android.ui.views.notification.NotificationSettingsScreen
import ygmd.kmpquiz.android.ui.views.quiz.QuizCreationScreen
import ygmd.kmpquiz.android.ui.views.quiz.QuizScreen
import ygmd.kmpquiz.android.ui.views.quiz.QuizSessionScreen
import ygmd.kmpquiz.android.ui.views.saved.SavedScreen
import ygmd.kmpquiz.android.ui.views.theme.QuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionNotification(this, this)
        setupNotificationChannel(this)

        setContent {
            QuizTheme(darkTheme = false) {
                val navController = rememberNavController()
                val startDestination = Home

                // gestion du deep link si l'activité est déjà existante
                LaunchedEffect(intent) {
                    intent.data?.lastPathSegment?.let { quizId ->
                        navController.navigate(PlayQuiz(quizId))
                    }
                }

                NavHost(navController = navController, startDestination = startDestination) {
                    composable<Home> {
                        HomeScreen(
                            onNavigateToFetch = { navController.navigate(Fetch) },
                            onNavigateToSaved = { navController.navigate(Saved) },
                            onNavigateToQuiz = { navController.navigate(Quizzes) },
                            onNavigateToNotifications = { navController.navigate(Settings) }
                        )
                    }

                    composable<Fetch> {
                        FetchScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable<Saved> {
                        SavedScreen(
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }

                    composable<Quizzes> {
                        QuizScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onCreateQuiz = { navController.navigate(QuizCreation) },
                            onStartQuiz = { quizId -> navController.navigate(PlayQuiz(quizId)) }
                        )
                    }

                    composable<QuizCreation> {
                        QuizCreationScreen(
                            onSavedQuiz = { navController.popBackStack() },
                            onCancelCreation = { navController.popBackStack() }
                        )
                    }

                    composable<PlayQuiz>(
                        deepLinks = listOf(navDeepLink { uriPattern = "myapp://quiz/{quizId}" })
                    ) { backStackEntry ->
                        val playQuiz = backStackEntry.toRoute<PlayQuiz>()
                        QuizSessionScreen(
                            quizId = playQuiz.quizId,
                            onNavigateHome = { navController.navigate(Home) },
                            onSessionFinished = { navController.navigate(Home) }
                        )
                    }

                    composable<Settings> {
                        NotificationSettingsScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }


    }

    // nécessaire pour singleTop pour recevoir le nouvel intent
    override fun onNewIntent(intent: Intent, caller: ComponentCaller) {
        super.onNewIntent(intent, caller)
        setIntent(intent)
    }
}