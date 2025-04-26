package ygmd.kmpquiz.ui.model

import androidx.compose.runtime.Composable
import ygmd.kmpquiz.ui.model.route.Route

data class NavigationItem(
    val name: String,
    val destination: Route,
    val screen: @Composable () -> Unit = {},
    val onNavigation: (Route) -> Unit  = {},
)

