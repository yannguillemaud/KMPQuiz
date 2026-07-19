package ygmd.kmpquiz.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Categories
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Category
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Home
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.PlaySession
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.QuizEditor
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute.Quizzes
import ygmd.kmpquiz.presentation.navigation.Navigator
import ygmd.kmpquiz.presentation.navigation.rememberNavigator
import ygmd.kmpquiz.presentation.screen.category.CategoriesScreen
import ygmd.kmpquiz.presentation.screen.home.HomeScreen
import ygmd.kmpquiz.presentation.screen.category.CategoryScreen
import ygmd.kmpquiz.presentation.screen.quiz.CategoryReviewScreen
import ygmd.kmpquiz.presentation.screen.quiz.DetailedSessionHistoryScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizEditorScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizSessionScreen
import ygmd.kmpquiz.presentation.screen.quiz.QuizzesScreen
import ygmd.kmpquiz.presentation.screen.quiz.SessionHistoryScreen
import ygmd.kmpquiz.presentation.theme.KMPQuizTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    quizId: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    navigator: Navigator = rememberNavigator(startRoute = Home),
) {
    val navItems = listOf(
        NavItem(Home, Icons.Outlined.Home, Icons.Filled.Home, "Home"),
        NavItem(Categories, Icons.Outlined.QuestionAnswer, Icons.Filled.QuestionAnswer, "Categories"),
        NavItem(Quizzes, Icons.Outlined.Quiz, Icons.Filled.Quiz, "Quizzes"),
    )

    // The bottom bar hides only while a quiz session (or its per-category review, which sits
    // inside the still-live finish flow) is on screen. Neither is a top-level route, so the
    // currently-visible entry of the active tab tells us exactly this.
    val shouldShowBottomBar = navigator.state.currentEntry !is PlaySession &&
            navigator.state.currentEntry !is KMPQuizRoute.CategoryReview

    LaunchedEffect(quizId) {
        if (quizId != null) {
            navigator.navigate(PlaySession(quizId, fromNotification = true))
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
                    // Floating, rounded, translucent capsule that hovers over the content.
                    // Modifier order (per design spec §4): horizontal margin -> system nav
                    // inset -> bottom gap -> soft shadow -> clip -> frosted fill -> outline.
                    //
                    // Documented off-token exception (navigationbar.md §6.3 / §B(a)): the capsule
                    // uses a full pill (RoundedCornerShape(percent = 50)) rather than the
                    // KMPQuizShapes.extraLarge (28dp) token — elegance is prioritised over
                    // single-source-of-truth radius here. Shadow / clip / border share it so they
                    // stay coincident.
                    val capsuleShape = RoundedCornerShape(percent = 50)

                    // Compose-idiomatic glassmorphism (per .claude/agents/developer.md
                    // "Glassmorphism technique" reference): a gradient semi-transparent fill and a
                    // gradient edge-highlight border replace the former flat single-alpha fill/border,
                    // giving the frosted capsule subtle depth (Compose has no native backdrop blur).
                    // Remembered so the brushes aren't rebuilt each recomposition; keyed on
                    // colorScheme so they stay reactive across light/dark themes.
                    val colorScheme = MaterialTheme.colorScheme
                    val capsuleFill = remember(colorScheme) {
                        // Overall opacity centred on the approved ~0.72 legibility level (0.80 -> 0.66).
                        Brush.linearGradient(
                            colors = listOf(
                                colorScheme.surface.copy(alpha = 0.80f),
                                colorScheme.surface.copy(alpha = 0.66f),
                            ),
                        )
                    }
                    val capsuleBorder = remember(colorScheme) {
                        // Brighter at the top-leading edge, fading elsewhere, for a glass edge highlight.
                        Brush.linearGradient(
                            colors = listOf(
                                colorScheme.outline.copy(alpha = 0.38f),
                                colorScheme.outline.copy(alpha = 0.12f),
                            ),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.PaddingMedium)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(bottom = Dimens.PaddingSmall)
                            .shadow(
                                elevation = 6.dp,
                                shape = capsuleShape,
                                clip = false,
                            )
                            .clip(capsuleShape)
                            .background(capsuleFill)
                            .border(
                                width = 1.dp,
                                brush = capsuleBorder,
                                shape = capsuleShape,
                            )
                    ) {
                        NavigationBar(
                            modifier = Modifier.height(Dimens.FloatingNavBarHeight),
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            // Insets are handled by the wrapper above; keep the bar itself flush.
                            windowInsets = WindowInsets(0, 0, 0, 0),
                        ) {
                            navItems.forEach { item ->
                                val selected = item.route == navigator.state.topLevelRoute

                                NavigationBarItem(
                                    icon = {
                                        // Standard M3: filled glyph when active, outlined when inactive.
                                        Icon(
                                            imageVector = if (selected) item.filledIcon else item.outlinedIcon,
                                            contentDescription = item.route.toString(),
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (selected) FontWeight.Bold else null,
                                        )
                                    },
                                    selected = selected,
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    ),
                                    onClick = { navigator.navigate(item.route) }
                                )
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                NavDisplay(
                    backStack = navigator.state.displayedBackStack,
                    onBack = { navigator.goBack() },
                    // State-holder first, then viewmodel-store: the latter scopes `koinViewModel()`
                    // per nav entry so navigating Category(A) -> Category(B), or replaying quiz
                    // sessions, never reuses a stale ViewModel across different nav args.
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator(),
                    ),
                    entryProvider = entryProvider {
                        entry<Home> {
                            HomeScreen(
                                onNavigateToCategories = { navigator.navigate(Categories) },
                                onNavigateToQuizzes = { navigator.navigate(Quizzes) },
                            )
                        }
                        entry<Categories> {
                            CategoriesScreen(
                                onNavigateToCategory = { navigator.navigate(Category(it)) },
                            )
                        }
                        entry<Category> { key ->
                            CategoryScreen(
                                categoryId = key.categoryId,
                                onNavigateBack = { navigator.goBack() },
                            )
                        }
                        entry<Quizzes> {
                            QuizzesScreen(
                                onNavigateToPlayQuiz = { navigator.navigate(PlaySession(quizId = it)) },
                                onNavigateToQuizEditor = { navigator.navigate(QuizEditor(it)) },
                                onNavigateToSessionHistory = { navigator.navigate(KMPQuizRoute.SessionHistory) },
                            )
                        }
                        entry<QuizEditor> { key ->
                            QuizEditorScreen(
                                isEditMode = key.quizId != null,
                                quizId = key.quizId,
                                onNavigateBack = { navigator.goBack() },
                            )
                        }
                        entry<PlaySession> { key ->
                            QuizSessionScreen(
                                quizId = key.quizId,
                                isNewSession = key.sessionId == null,
                                sessionId = key.sessionId,
                                fromNotification = key.fromNotification,
                                // Both the mid-quiz back arrow and the finish-screen arrow land on
                                // the Quizzes tab root, clearing this session off whichever tab
                                // pushed it (Quizzes for manual play, Home for the deep-link path).
                                onNavigateBack = { navigator.navigateToTopLevelClearingCurrent(Quizzes) },
                                onFinishQuiz = { navigator.navigateToTopLevelClearingCurrent(Quizzes) },
                                onNavigateToCategoryReview = { sessionId, categoryId, categoryName ->
                                    navigator.navigate(
                                        KMPQuizRoute.CategoryReview(sessionId, categoryId, categoryName)
                                    )
                                },
                            )
                        }
                        entry<KMPQuizRoute.SessionHistory> {
                            SessionHistoryScreen(
                                onNavigateBack = { navigator.goBack() },
                                onNavigateToSessionDetails = {
                                    navigator.navigate(KMPQuizRoute.SessionDetails(it))
                                },
                                onContinueSession = {},
                            )
                        }
                        entry<KMPQuizRoute.SessionDetails> { key ->
                            DetailedSessionHistoryScreen(
                                sessionId = key.sessionId,
                                // Pre-existing quirk (preserved): lands on the Quizzes tab root
                                // rather than back to SessionHistory. `clearCurrentToRootAndSwitch`
                                // clears this tab (SessionDetails sits 3-deep) fully to root first.
                                onNavigateBack = { navigator.navigateToTopLevelClearingCurrent(Quizzes) },
                            )
                        }
                        entry<KMPQuizRoute.CategoryReview> { key ->
                            CategoryReviewScreen(
                                sessionId = key.sessionId,
                                categoryId = key.categoryId,
                                categoryName = key.categoryName,
                                // Plain goBack(): this screen sits inside the still-live finish
                                // flow, so popping returns to the finish screen with its state
                                // intact (unlike SessionDetails' clear-to-root).
                                onNavigateBack = { navigator.goBack() },
                            )
                        }
                    },
                )
            }
        }
    }
}

/**
 * A bottom-navigation tab: its route, both icon variants (outlined = inactive,
 * filled = active per standard M3), and its label.
 */
private data class NavItem(
    val route: KMPQuizRoute,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector,
    val title: String,
)
