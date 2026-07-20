package ygmd.kmpquiz.presentation.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.core.domain.event.Event.ShowSnackbar
import ygmd.kmpquiz.presentation.composable.fetch.FetcherSourceRow
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.theme.ExtendedTheme
import ygmd.kmpquiz.presentation.viewModel.home.DownloadIntent
import ygmd.kmpquiz.presentation.viewModel.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToCategories: () -> Unit = {},
    onNavigateToQuizzes: () -> Unit = {},
    viewModel: HomeViewModel = koinViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val homeState by viewModel.contentFlow.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            if (event is ShowSnackbar) {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Home", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ExtendedTheme.colors.tonalBackground,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = ExtendedTheme.colors.tonalBackground,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = Dimens.PaddingSmall)
                // Reserve space so the last source card clears the floating nav capsule.
                .padding(bottom = Dimens.BottomNavPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            // ── HERO OVERVIEW ─────────────────────────────────────────────────
            // Warm gradient header carrying the greeting + the 3 overview stats.
            HomeHeroCard(
                categoriesCount = homeState.totalCategoriesCount,
                questionsCount = homeState.totalQandasCount,
                quizzesCount = homeState.totalQuizCount,
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
            )

            // ── QUICK ACTIONS ─────────────────────────────────────────────────
            // "Start a Quiz" leads as the primary CTA-style row, "Browse Categories"
            // follows. (The former full-width CTA button + Session History row were
            // merged into this two-row list.)
            HomeSectionLabel("Quick Actions")
            Column(
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                HomeQuickActionRow(
                    icon = Icons.Default.PlayArrow,
                    label = "Start a Quiz",
                    onClick = onNavigateToQuizzes,
                )
                HomeQuickActionRow(
                    icon = Icons.AutoMirrored.Filled.LibraryBooks,
                    label = "Browse Categories",
                    onClick = onNavigateToCategories,
                )
            }

            // ── AVAILABLE SOURCES ─────────────────────────────────────────────
            // "See all →" affordance intentionally deferred: there is no dedicated
            // Sources route yet (see NavigationRoutes.kt). Only the count is surfaced.
            HomeSectionLabel(
                if (homeState.sources.size > 1) {
                    "Available Sources (${homeState.sources.size})"
                } else {
                    "Available Sources"
                }
            )
            Column(
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
            ) {
                homeState.sources.forEach { (id, fetcher) ->
                    FetcherSourceRow(
                        modifier = Modifier.fillMaxWidth(),
                        name = fetcher.name,
                        status = fetcher.status,
                        error = fetcher.error,
                        onFetchInvoke = { viewModel.processIntent(DownloadIntent.Fetch(id)) }
                    )
                }
            }
        }
    }
}

/**
 * Hero header — Design Cycle-2 "A3: Greeting band + plain stat shelf".
 *
 * Two zones inside one `shapes.large`-clipped card: a slim gradient greeting band
 * (`primary→secondary`, the praised brush kept at full strength but confined to a ribbon)
 * and a neutral `surface` stat shelf below. Concentrating the color in a small band lets the
 * light theme breathe white ("bien doser") without abandoning the gradient identity.
 *
 * Theme safety: the band text binds to `onPrimary` (dark-on-pastel in dark mode,
 * white-on-indigo in light mode) and the shelf is neutral `surface`/`onSurface`, so contrast
 * holds in both themes with no `isSystemInDarkTheme()` branch and no hardcoded `Color.White`.
 */
@Composable
private fun HomeHeroCard(
    categoriesCount: Int,
    questionsCount: Int,
    quizzesCount: Int,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    // Remembered so the brush isn't rebuilt each recomposition; keyed on colorScheme
    // so it stays reactive across light/dark themes.
    val heroGradient = remember(colorScheme) {
        Brush.linearGradient(
            colors = listOf(colorScheme.primary, colorScheme.secondary),
        )
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = colorScheme.surface,
        shadowElevation = Dimens.CardElevation,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Gradient greeting band (~1/3 height): color concentrated here only.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(heroGradient)
                    .padding(
                        horizontal = Dimens.PaddingLarge,
                        vertical = Dimens.PaddingMedium,
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Welcome back",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(Dimens.IconSize),
                )
            }
            // Neutral stat shelf: white background, tinted dot per stat.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingLarge),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                HomeHeroStat(
                    count = categoriesCount,
                    label = "Categories",
                    accentColor = colorScheme.tertiary,
                    modifier = Modifier.weight(1f),
                )
                HomeHeroStat(
                    count = questionsCount,
                    label = "Questions",
                    accentColor = colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
                HomeHeroStat(
                    count = quizzesCount,
                    label = "Quizzes",
                    accentColor = colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

/**
 * One overview stat inside the hero shelf: the formatted count above a tinted dot + label.
 * The numeral stays neutral (`onSurface`); the small [accentColor] dot carries the accent
 * (decorative only, so coral on Categories is fine per the AA-Large constraint). Tabular
 * figures (`tnum`) keep multi-digit counts aligned; `maxLines = 1` plus [formatStatCount]
 * guarantee the value never wraps nor pushes the label.
 */
@Composable
private fun HomeHeroStat(
    count: Int,
    label: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.ChoiceVerticalPadding),
    ) {
        Text(
            text = formatStatCount(count),
            style = MaterialTheme.typography.headlineMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.ChoiceVerticalPadding),
        ) {
            Box(
                modifier = Modifier
                    .size(Dimens.DotIndicatorSize)
                    .clip(CircleShape)
                    .background(accentColor),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

/**
 * Full-width tappable quick-action row: leading icon, label, trailing chevron.
 * Tonal background + rounded medium shape, min height for a comfortable tap target.
 */
@Composable
private fun HomeQuickActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = Dimens.ListRowMinHeight)
                .padding(horizontal = Dimens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(Dimens.IconSize),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.IconSize),
            )
        }
    }
}

@Composable
private fun HomeSectionLabel(label: String) {
    Text(
        text = label.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(
            horizontal = Dimens.PaddingMedium,
            vertical = Dimens.PaddingSmall
        )
    )
}

/**
 * Collapses overview counts above 999 to "999+" so a wide count never clips or breaks
 * the hero layout (fixes the former 48dp `CircleShape` StatBadge clipping 3-digit values).
 * 999 is the practical ceiling for the `weight(1f)` stat column at `headlineMedium`: a 4th
 * digit still fits the tabular-figure width, a 5th would risk clipping on narrow devices.
 */
private fun formatStatCount(count: Int): String = if (count > 999) "999+" else count.toString()
