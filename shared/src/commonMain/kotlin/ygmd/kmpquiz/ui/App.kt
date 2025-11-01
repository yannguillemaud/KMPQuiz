package ygmd.kmpquiz.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ygmd.kmpquiz.navigation.AppNavigationState
import ygmd.kmpquiz.ui.model.route.Route
import ygmd.kmpquiz.ui.model.route.Route.Fetch
import ygmd.kmpquiz.ui.model.route.Route.Home
import ygmd.kmpquiz.ui.model.route.Route.PlayQuiz
import ygmd.kmpquiz.ui.model.route.Route.Qandas
import ygmd.kmpquiz.ui.model.route.Route.QuizSettings
import ygmd.kmpquiz.ui.model.route.Route.Quizzes
import ygmd.kmpquiz.ui.screen.fetch.FetchScreen
import ygmd.kmpquiz.ui.screen.home.HomeScreen
import ygmd.kmpquiz.ui.screen.qandas.QandaCreationScreen
import ygmd.kmpquiz.ui.screen.qandas.QandaEditScreen
import ygmd.kmpquiz.ui.screen.qandas.SavedCategoriesScreen
import ygmd.kmpquiz.ui.screen.qandas.SavedCategoryScreen
import ygmd.kmpquiz.ui.screen.quiz.PlayQuizScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizCreationScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizSettingsScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.ui.theme.KMPQuizTheme


@Composable
fun App(
    navController: NavHostController = rememberNavController(),
) {
    val navItems: List<Pair<Route, ImageVector>> = listOf(
        Home to Icons.Default.Home,
        Fetch to Icons.Default.AddShoppingCart,
        Qandas to Icons.Default.DownloadDone,
        Quizzes to Icons.Default.Quiz,
//        Friends to Icons.Default.Person // COMING SOON
    )

    val navEvent by AppNavigationState.initialNavEvent.collectAsState()
    LaunchedEffect(navEvent) {
        navEvent?.let {
            navController.navigate(PlayQuiz(it.quizId))
            AppNavigationState.consumeNavEvent()
        }
    }

    KMPQuizTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = Color.Transparent) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        navItems.forEach { (route, icon) ->
                            val selected =
                                currentDestination?.hierarchy?.any { it.route == route.name } == true
                            val scale by animateFloatAsState(if (selected) 1.2f else 1.0f)
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = route.name,
                                        modifier = Modifier.scale(scale)
                                    )
                                },
                                label = { Text(route.name) },
                                selected = selected,
                                onClick = { navController.navigate(route) }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                    NavHost(
                        navController = navController,
                        startDestination = Home,
                    ) {
                        composable<Home> {
                            HomeScreen()
                        }

                        composable<Fetch> {
                            FetchScreen()
                        }

                        composable<Qandas> {
                            SavedCategoriesScreen(
                                onNavigateToSavedByCategory = { category ->
                                    navController.navigate(
                                        Route.Category(category)
                                    )
                                },
                            )
                        }

                        composable<Route.Category> { backStackEntry ->
                            val category = backStackEntry.toRoute<Route.Category>().categoryId
                            SavedCategoryScreen(
                                categoryId = category,
                                onNavigateToEdit = {
                                    navController.navigate(Route.QandaEdit(it))
                                },
                                onNavigateToQandaCreation = { categoryId ->
                                    navController.navigate(Route.QandaCreation(categoryId))
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.QandaCreation> { backStackEntry ->
                            val route = backStackEntry.toRoute<Route.QandaCreation>()
                            QandaCreationScreen(
                                initialCategoryId = route.categoryId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.QandaEdit> { backStackEntry ->
                            val qandaId = backStackEntry.toRoute<Route.QandaEdit>().qandaId
                            QandaEditScreen(
                                qandaId = qandaId,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Quizzes> {
                            QuizzesScreen(
                                onNavigateToQuizCreation = { navController.navigate(Route.QuizCreation) },
                                onNavigateToQuizSettings = { quizId ->
                                    navController.navigate(
                                        QuizSettings(quizId)
                                    )
                                },
                                onNavigateToPlayQuiz = { quizId ->
                                    navController.navigate(
                                        PlayQuiz(
                                            quizId
                                        )
                                    )
                                },
                            )
                        }

                        composable<QuizSettings> { backStackEntry ->
                            val quizId = backStackEntry.toRoute<QuizSettings>().quizId
                            QuizSettingsScreen(
                                quizId = quizId,
                                onFinished = { navController.navigate(Quizzes) },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.QuizCreation> {
                            QuizCreationScreen(
                                onFinished = { navController.popBackStack() },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<PlayQuiz> { backStackEntry ->
                            val quizId = backStackEntry.toRoute<PlayQuiz>().quizId
                            PlayQuizScreen(
                                quizId = quizId,
                                onFinished = { navController.navigate(Quizzes) }
                            )
                        }
                    }
                }
            }
        }
    }
}
