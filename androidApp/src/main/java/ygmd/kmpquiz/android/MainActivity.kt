package ygmd.kmpquiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import org.koin.android.ext.koin.androidContext
import ygmd.kmpquiz.android.notification.NotificationUtils.requestPermissionNotification
import ygmd.kmpquiz.android.notification.NotificationUtils.setupNotificationChannel
import ygmd.kmpquiz.di.initKoin
import ygmd.kmpquiz.ui.screen.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionNotification(this, this)
        setupNotificationChannel(this)
        initKoin {
            androidContext(this@MainActivity)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            App()
        }
/*
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
*/
    }
}