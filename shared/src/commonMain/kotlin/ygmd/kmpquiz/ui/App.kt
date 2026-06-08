package ygmd.kmpquiz.ui

// import ygmd.kmpquiz.events.navigation.AppNavigationState -> SUPPRIMÉ
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import ygmd.kmpquiz.ui.model.route.Route
import ygmd.kmpquiz.ui.model.route.Route.Categories
import ygmd.kmpquiz.ui.model.route.Route.Category
import ygmd.kmpquiz.ui.model.route.Route.Home
import ygmd.kmpquiz.ui.model.route.Route.PlaySession
import ygmd.kmpquiz.ui.model.route.Route.QuizEditor
import ygmd.kmpquiz.ui.model.route.Route.Quizzes
import ygmd.kmpquiz.ui.screen.category.CategoriesScreen
import ygmd.kmpquiz.ui.screen.home.HomeScreen
import ygmd.kmpquiz.ui.screen.qandas.CategoryScreen
import ygmd.kmpquiz.ui.screen.quiz.DetailedSessionHistoryScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizEditorScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizSessionScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.ui.screen.quiz.SessionHistoryScreen
import ygmd.kmpquiz.ui.theme.KMPQuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    quizId: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    val navItems = listOf(
        Home to Icons.Default.Home,
        Categories to Icons.Default.QuestionAnswer,
        Quizzes to Icons.Default.Quiz,
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val shouldShowBottomBar = navItems.any { (route, _) ->
        currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    }

    LaunchedEffect(quizId) {
        if (quizId != null) {
            navController.navigate(PlaySession(quizId)) {
                launchSingleTop = true
            }
            onDeepLinkConsumed()
        }
    }

    KMPQuizTheme {
        Scaffold(
            bottomBar = {
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
                                selected = selected,
                                onClick = {
                                    navController.navigate(route) {
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
                    composable<Home> { HomeScreen() }
                    composable<Categories> {
                        CategoriesScreen(onNavigateToCategory = {
                            navController.navigate(Category(it))
                        })
                    }
                    composable<Category> {
                        CategoryScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable<Quizzes> {
                        QuizzesScreen(
                            onNavigateToPlayQuiz = { navController.navigate(PlaySession(quizId = it)) },
                            onNavigateToQuizEditor = {
                                navController.navigate(QuizEditor(it))
                            },
                            onNavigateToSessionHistory = {
                                navController.navigate(Route.SessionHistory)
                            }
                        )
                    }
                    composable<QuizEditor> { backStackEntry ->
                        val quizId = backStackEntry.toRoute<QuizEditor>().quizId
                        QuizEditorScreen(
                            isEditMode = quizId != null,
                            quizId = quizId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable<PlaySession> {
                        val sessionId = it.toRoute<PlaySession>().sessionId
                        QuizSessionScreen(
                            isNewSession = sessionId == null,
                            sessionId = sessionId,
                            onShowHistory = { navController.navigate(Route.SessionHistory) },
                            onNavigateToResults = {
                                navController.navigate(Route.SessionDetails(it))
                            }
                        )
                    }
                    composable<Route.SessionHistory> {
                        SessionHistoryScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToSessionDetails = {
                                navController.navigate(Route.SessionDetails(it))
                            },
                            onContinueSession = {
                                navController.navigate(PlaySession(sessionId = it))
                            }
                        )
                    }
                    composable<Route.SessionDetails> {
                        DetailedSessionHistoryScreen(
                            sessionId = it.toRoute<Route.SessionDetails>().sessionId,
                            onNavigateBack = { navController.navigate(Route.Quizzes) }
                        )
                    }
                }
            }
        }
    }
}
