package ygmd.kmpquiz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import ygmd.kmpquiz.core.domain.route.KMPQuizRoute

/**
 * Multi-back-stack navigation state for the three bottom-nav tabs
 * (Home / Categories / Quizzes). Each tab owns an independent [NavBackStack], so switching
 * tabs preserves that tab's own history. This is the Navigation 3 replacement for Nav2's
 * `popUpTo(start){saveState=true}; launchSingleTop; restoreState=true` idiom, which has no
 * Nav3 equivalent.
 *
 * Invariant: only the three tab routes are ever top-level. Every detail route
 * (`Category`, `QuizEditor`, `PlaySession`, `SessionHistory`, `SessionDetails`) is pushed
 * onto the *current* tab's stack. `PlaySession` in particular is never a top-level route, so
 * the bottom bar can hide itself whenever the visible entry is a `PlaySession`, regardless of
 * which tab launched it (Quizzes for a manual play, Home for the notification deep-link path).
 *
 * Back-to-exit: a tab stays mounted (preserving its state) only while it remains reachable by
 * *forward* navigation (tab switch / push). The moment the user backs out to a tab's own root,
 * [pop] drops that tab from [tabOrder] and falls back to the previously-active tab, so repeated
 * system back eventually unwinds [displayedBackStack] to size 1 and exits the app — the official
 * Nav3 `removeLast()` recipe. Note this is a behavior *change* from a fixed-start model (back from
 * a tab root always landing on the start tab): back now steps back through visited tabs in
 * reverse-visit order before exiting, e.g. `Home → Quizzes → Home → back` lands on `Quizzes`, not
 * an exit. See `docs/refactorization/2026-07-18-nav3-migration.md`
 * and `docs/refactorization/back-model-exit-followup.md` for the history of this fix.
 */
class NavigationState(
    val startRoute: KMPQuizRoute,
    private val backStacks: Map<KMPQuizRoute, NavBackStack<NavKey>>,
) {
    val topLevelRoutes: Set<KMPQuizRoute> = backStacks.keys

    var topLevelRoute: KMPQuizRoute by mutableStateOf(startRoute)
        private set

    /**
     * Tab visitation order, "least-recently-active first"; the active tab is always last, so its
     * top entry ends up last in [flatten] — the position `NavDisplay`/its decorators treat as "on
     * top". Seeded with **only** [startRoute]; each other tab is appended on its first
     * [switchTopLevel] and then stays for the lifetime of the state. This mirrors the official
     * multi-back-stack `TopLevelBackStack`, which lazily populates its ordered map (start key only,
     * others added on first visit).
     *
     * The lazy seed is deliberate: seeding all three would make [displayedBackStack] always
     * size ≥ 3, and `NavDisplay` enables its back handler whenever the rendered stack has more than
     * one entry (`enabled = !scene.previousEntries.isEmpty()`), so system back would be intercepted
     * even at the start-tab root and could never exit the app. Seeding only the start tab keeps the
     * fresh-launch stack size 1 so back-to-exit still works. Once a tab is visited it stays in this
     * list until [pop] removes it again at that tab's own root — see the class KDoc.
     *
     * Must be declared before [displayedBackStack]: the latter's initializer calls [flatten],
     * which reads this list (Kotlin initializes properties in declaration order).
     */
    private val tabOrder: MutableList<KMPQuizRoute> = mutableListOf(startRoute)

    /**
     * Flattened key list that `NavDisplay` renders. The start tab is always mounted; every other
     * tab is mounted from its first visit onward (it stays in [tabOrder] once appended). Because a
     * visited tab's keys never leave this list, the two `NavDisplay` decorators never tear down its
     * `ViewModelStore`/`SaveableStateHolder` slots — so switching away from and back to a visited
     * tab preserves its state. The active tab's stack is last, so its top entry is what `NavDisplay`
     * shows.
     */
    val displayedBackStack: SnapshotStateList<NavKey> =
        mutableStateListOf<NavKey>().apply { addAll(flatten()) }

    private val currentStack: NavBackStack<NavKey>
        get() = backStacks.getValue(topLevelRoute)

    /** The entry the user currently sees: the top of the active tab's stack. */
    val currentEntry: NavKey
        get() = currentStack.last()

    /** Switch to an existing tab, preserving its stack and moving it to the end of the visitation order. */
    fun switchTopLevel(route: KMPQuizRoute) {
        topLevelRoute = route
        // Re-seat the active tab at the end of the visitation order so [flatten] renders its top
        // entry last (what NavDisplay shows). `remove` no-ops on a tab's first visit (not yet in
        // the list); `add` appends it. Once added, a tab stays mounted for the state's lifetime.
        tabOrder.remove(route)
        tabOrder.add(route)
        sync()
    }

    /** Push a detail route onto the current tab's stack. */
    fun push(route: KMPQuizRoute) {
        currentStack.add(route)
        sync()
    }

    /**
     * Pop the current tab's stack. At a tab's own root (nothing pushed on it), drop that tab out
     * of [tabOrder] entirely and switch to the previously-active tab — this is what lets system
     * back eventually reach [displayedBackStack] size 1 and exit the app, instead of the tab
     * staying mounted forever (see [tabOrder] KDoc). Only the tab's *root* entry is affected;
     * a tab with a pushed child (e.g. an in-progress `QuizEditor` form) never reaches this branch
     * until that child is popped first, so mid-screen state is unaffected by this fallback.
     */
    fun pop() {
        if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.lastIndex)
            sync()
        } else if (tabOrder.size > 1) {
            tabOrder.removeAt(tabOrder.lastIndex)
            topLevelRoute = tabOrder.last()
            sync()
        }
        // else: only the start tab remains, at its root — nothing left to pop; let the system
        // back gesture/button proceed to exit the app.
    }

    /**
     * Clear the currently active tab **down to its root**, then switch to [route]. Reproduces the
     * Nav2 "clear the PlaySession/SessionDetails entry and land on Quizzes" behavior without a
     * global `popUpTo` — it empties whichever tab pushed the detail entries (Quizzes for a manual
     * play, the start tab for the notification deep-link path) back to that tab's root, so callers
     * land on [route]'s root regardless of how deep the source tab was (`PlaySession` at depth 2,
     * `SessionDetails` at depth 3).
     */
    fun clearCurrentToRootAndSwitch(route: KMPQuizRoute) {
        while (currentStack.size > 1) currentStack.removeAt(currentStack.lastIndex)
        switchTopLevel(route)
    }

    private fun sync() {
        displayedBackStack.clear()
        displayedBackStack.addAll(flatten())
    }

    /** Concatenate every tab's full back stack in [tabOrder]; the active tab's stack ends up last. */
    private fun flatten(): List<NavKey> =
        tabOrder.flatMap { backStacks.getValue(it) }
}

/**
 * Thin façade over [NavigationState] that the screens' callback lambdas resolve to. Screens
 * never see this type directly — `App` translates their `onNavigateToX` / `onNavigateBack`
 * callbacks into these calls.
 */
class Navigator(val state: NavigationState) {
    /** Top-level routes switch tabs (preserving state); everything else is a push. */
    fun navigate(route: KMPQuizRoute) {
        if (route in state.topLevelRoutes) state.switchTopLevel(route) else state.push(route)
    }

    fun goBack() = state.pop()

    fun navigateToTopLevelClearingCurrent(route: KMPQuizRoute) =
        state.clearCurrentToRootAndSwitch(route)
}

/**
 * Builds a [Navigator] backed by one [rememberNavBackStack] per bottom-nav tab. The three
 * `rememberNavBackStack` calls are unconditional (fixed tab set), so they satisfy the rules
 * of Composable invocation.
 */
@Composable
fun rememberNavigator(startRoute: KMPQuizRoute = KMPQuizRoute.Home): Navigator {
    val homeStack = rememberNavBackStack(KMPQuizRoute.Home)
    val categoriesStack = rememberNavBackStack(KMPQuizRoute.Categories)
    val quizzesStack = rememberNavBackStack(KMPQuizRoute.Quizzes)
    return remember {
        Navigator(
            NavigationState(
                startRoute = startRoute,
                backStacks = linkedMapOf(
                    KMPQuizRoute.Home to homeStack,
                    KMPQuizRoute.Categories to categoriesStack,
                    KMPQuizRoute.Quizzes to quizzesStack,
                ),
            ),
        )
    }
}
