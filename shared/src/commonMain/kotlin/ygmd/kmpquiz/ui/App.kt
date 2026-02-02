package ygmd.kmpquiz.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ygmd.kmpquiz.navigation.AppNavigationState
import ygmd.kmpquiz.ui.model.route.Route
import ygmd.kmpquiz.ui.model.route.Route.Categories
import ygmd.kmpquiz.ui.model.route.Route.Fetch
import ygmd.kmpquiz.ui.model.route.Route.Home
import ygmd.kmpquiz.ui.model.route.Route.PlayQuiz
import ygmd.kmpquiz.ui.model.route.Route.QuizSettings
import ygmd.kmpquiz.ui.model.route.Route.Quizzes
import ygmd.kmpquiz.ui.screen.category.CategoriesScreen
import ygmd.kmpquiz.ui.screen.fetch.FetchScreen
import ygmd.kmpquiz.ui.screen.home.HomeScreen
import ygmd.kmpquiz.ui.screen.qandas.CategoryScreen
import ygmd.kmpquiz.ui.screen.qandas.QandaCreationScreen
import ygmd.kmpquiz.ui.screen.qandas.QandaEditScreen
import ygmd.kmpquiz.ui.screen.quiz.PlayQuizScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizCreationScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizSettingsScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.ui.theme.KMPQuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    navController: NavHostController = rememberNavController(),
) {
    val navItems = listOf(
        Home to Icons.Default.Home,
        Fetch to Icons.Default.AddShoppingCart,
        Categories to Icons.Default.DownloadDone,
        Quizzes to Icons.Default.Quiz,
    )

    // On récupère la destination actuelle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Déterminer si on doit afficher la BottomBar :
    // On ne l'affiche que si on est sur l'un des 4 onglets principaux
    val shouldShowBottomBar = navItems.any { (route, _) ->
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    }

    val navEvent by AppNavigationState.initialNavEvent.collectAsState()
    LaunchedEffect(navEvent) {
        navEvent?.let {
            navController.navigate(PlayQuiz(it.quizId))
            AppNavigationState.consumeNavEvent()
        }
    }

    KMPQuizTheme {
        Scaffold(
            bottomBar = {
                // Animation pour cacher/montrer la barre proprement
                AnimatedVisibility(
                    visible = shouldShowBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it }),
                ) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        navItems.forEach { (route, icon) ->
                            val selected =
                                currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
                            val scale by animateFloatAsState(if (selected) 1.2f else 1.0f)

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = route.toString(),
                                        modifier = Modifier.scale(scale)
                                    )
                                },
                                label = { Text(route::class.simpleName ?: "") },
                                selected = selected,
                                onClick = {
                                    navController.navigate(route) {
                                        // Évite d'empiler les instances des onglets principaux
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
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

                    composable<Categories> {
                        CategoriesScreen(
                            onNavigateToCategory = { category ->
                                navController.navigate(
                                    Route.Category(category)
                                )
                            },
                        )
                    }

                    composable<Route.Category> { backStackEntry ->
                        val category = backStackEntry.toRoute<Route.Category>().categoryId
                        CategoryScreen(
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
                            onFinished = {
                                navController.navigate(Quizzes) {
                                    popUpTo(PlayQuiz::class) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
