package ygmd.kmpquiz.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ygmd.kmpquiz.ui.model.route.Route
import ygmd.kmpquiz.ui.model.route.Route.Fetch
import ygmd.kmpquiz.ui.model.route.Route.Home
import ygmd.kmpquiz.ui.model.route.Route.Qandas
import ygmd.kmpquiz.ui.model.route.Route.Quizzes
import ygmd.kmpquiz.ui.model.route.Route.Settings
import ygmd.kmpquiz.ui.screen.fetch.FetchScreen
import ygmd.kmpquiz.ui.screen.home.HomeScreen
import ygmd.kmpquiz.ui.screen.qandas.QandasScreen
import ygmd.kmpquiz.ui.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.ui.theme.QuizTheme

@Composable
fun App(navController: NavHostController = rememberNavController()) {
    val navItems: List<Pair<Route, ImageVector>> = listOf(
        Home to Icons.Default.Home,
        Fetch to Icons.Default.AddShoppingCart,
        Qandas to Icons.Default.DownloadDone,
        Quizzes to Icons.Default.Quiz,
        Settings to Icons.Default.Settings,
    )

    QuizTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Scaffold(
                bottomBar = {
                    NavigationBar(containerColor = Color.Transparent){
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        navItems.forEach { (route, icon) ->
                            NavigationBarItem(
                                icon = { Icon(imageVector = icon, contentDescription = route.name) },
                                selected = currentDestination?.hierarchy?.any { it.route == route.name } == true,
                                onClick = { navController.navigate(route) }
                            )
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)){
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
                            QandasScreen()
                        }

                        composable<Quizzes> {
                            QuizzesScreen()
                        }
                    }
                }
            }
        }
    }
}