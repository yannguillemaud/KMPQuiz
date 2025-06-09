package ygmd.kmpquiz.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ygmd.kmpquiz.android.ui.composable.StatisticsScreen
import ygmd.kmpquiz.android.ui.views.fetch.FetchScreen
import ygmd.kmpquiz.android.ui.views.home.HomeScreen
import ygmd.kmpquiz.android.ui.views.saved.SavedScreen
import ygmd.kmpquiz.android.ui.views.settings.SettingsScreen
import ygmd.kmpquiz.android.ui.views.theme.QuizTheme
import ygmd.kmpquiz.viewModel.save.QuizStats

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
                            onNavigateToStats = { navController.navigate(Statistics) },
                            onNavigateToParams = { navController.navigate(Settings) }
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
                            onStartQuiz = { qandas ->
                                // TODO: Naviguer vers l'écran de quiz avec les qandas sélectionnés
                                // navController.navigate(QuizScreen(qandas))
                            }
                        )
                    }

                    composable<Statistics> {
                        // TODO: Implémenter l'écran de statistiques globales
                        StatisticsScreen(
                            stats = QuizStats(),
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable<Settings> {
                        // TODO: Implémenter l'écran de statistiques globales
                        SettingsScreen(
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