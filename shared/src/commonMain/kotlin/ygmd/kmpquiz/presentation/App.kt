package ygmd.kmpquiz.presentation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Categories
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Category
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Home
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.PlaySession
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.QuizEditor
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Quizzes
import ygmd.kmpquiz.presentation.screen.category.CategoriesScreen
import ygmd.kmpquiz.presentation.screen.home.HomeScreen
import ygmd.kmpquiz.presentation.screen.category.CategoryScreen
import ygmd.kmpquiz.presentation.screen.quiz.DetailedSessionHistoryScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizEditorScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizSessionScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.presentation.screen.quiz.SessionHistoryScreen
import ygmd.kmpquiz.presentation.theme.KMPQuizTheme
import kotlin.collections.listOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    quizId: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    val navItems = listOf(
        Home withIcon Icons.Default.Home withTitle "Home",
        Categories withIcon Icons.Default.QuestionAnswer withTitle "Categories",
        Quizzes withIcon Icons.Default.Quiz withTitle "Quizzes"
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
                        navItems.forEach { (route, icon, title) ->
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
                                label = {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.labelSmall
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
                                navController.navigate(KMPQuizRoute.SessionHistory)
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
                            onShowHistory = { navController.navigate(KMPQuizRoute.SessionHistory) },
                            onNavigateToResults = {
                                navController.navigate(KMPQuizRoute.SessionDetails(it))
                            }
                        )
                    }
                    composable<KMPQuizRoute.SessionHistory> {
                        SessionHistoryScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToSessionDetails = {
                                navController.navigate(KMPQuizRoute.SessionDetails(it))
                            },
                            onContinueSession = {
                                navController.navigate(PlaySession(sessionId = it))
                            }
                        )
                    }
                    composable<KMPQuizRoute.SessionDetails> {
                        DetailedSessionHistoryScreen(
                            sessionId = it.toRoute<KMPQuizRoute.SessionDetails>().sessionId,
                            onNavigateBack = { navController.navigate(Quizzes) }
                        )
                    }
                }
            }
        }
    }
}

private infix fun KMPQuizRoute.withIcon(icon: ImageVector) = Pair(this, icon)
private infix fun Pair<KMPQuizRoute, ImageVector>.withTitle(title: String) =
    Triple(first, second, title)