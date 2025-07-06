package ygmd.kmpquiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import ygmd.kmpquiz.android.ui.views.fetch.FetchScreen
import ygmd.kmpquiz.android.ui.views.home.HomeScreen
import ygmd.kmpquiz.android.ui.views.notification.NotificationSettingsScreen
import ygmd.kmpquiz.android.ui.views.quiz.QuizScreen
import ygmd.kmpquiz.android.ui.views.saved.SavedScreen
import ygmd.kmpquiz.android.ui.views.theme.QuizTheme

class Main : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizTheme(darkTheme = false) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Home) {
                    composable<Home> {
                        HomeScreen(
                            onNavigateToFetch = { navController.navigate(Fetch) },
                            onNavigateToSaved = { navController.navigate(Saved) },
                            onNavigateToQuiz = { navController.navigate(Quiz) },
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
                            onStartQuiz = { qandaIds ->
                                navController.navigate(Quiz(qandaIds))
                            }
                        )
                    }

                    composable<Quiz> { backStackEntry ->
                        val quiz = backStackEntry.toRoute<Quiz>()
                        QuizScreen(
                            qandaIds = quiz.qandaIds,
                            onNavigateBack = { navController.popBackStack() }
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
}

// Routes de navigation
@Serializable
object Home

@Serializable
object Fetch

@Serializable
object Saved

@Serializable
object Statistics

@Serializable
object Settings

@Serializable
data class Quiz(val qandaIds: List<Long>)