package ygmd.kmpquiz.presentation.screen.category

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.presentation.composable.EmptyState
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.category.CategoryViewModel
import ygmd.kmpquiz.presentation.viewModel.category.PendingCategoriesDeletion
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategoryWithCount
import kotlin.math.abs

private const val GRID_THRESHOLD = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewmodel: CategoryViewModel = koinViewModel(),
    onNavigateToCategory: (categoryId: String) -> Unit = {},
) {
    val categoriesState by viewmodel.categories.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manual list/grid toggle. GRID_THRESHOLD only *seeds* the initial value once
    // categories first arrive; afterwards only the toggle mutates it (never re-keyed
    // on categories.size). Stays null until data is loaded so the threshold seeds off
    // the real size, not the empty loading state, then survives config changes.
    var useGrid by rememberSaveable { mutableStateOf<Boolean?>(null) }
    LaunchedEffect(categoriesState.categories.isNotEmpty()) {
        if (useGrid == null && categoriesState.categories.isNotEmpty()) {
            useGrid = categoriesState.categories.size > GRID_THRESHOLD
        }
    }
    val isGrid = useGrid ?: false
    val selectionMode = categoriesState.selectionMode

    // Off-selection: tap navigates, long-press enters selection.
    // In-selection: tap toggles the item (no navigation), long-press is a no-op reselect.
    val onItemClick: (String) -> Unit = { id ->
        if (selectionMode) viewmodel.toggleSelection(id) else onNavigateToCategory(id)
    }
    val onItemLongClick: (String) -> Unit = { id -> viewmodel.enterSelection(id) }

    Scaffold(
        topBar = {
            // Swap the whole bar between browsing and contextual-selection states.
            Crossfade(targetState = selectionMode, label = "categoryTopBar") { inSelection ->
                if (inSelection) {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = viewmodel::clearSelection) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Clear selection",
                                    modifier = Modifier.size(Dimens.IconSize),
                                )
                            }
                        },
                        title = {
                            Text(
                                "${categoriesState.selectedIds.size} selected",
                                fontWeight = FontWeight.Bold,
                            )
                        },
                        actions = {
                            IconButton(onClick = viewmodel::requestDeleteSelected) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "Delete selected categories",
                                    modifier = Modifier.size(Dimens.IconSize),
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text("Categories", fontWeight = FontWeight.Bold) },
                        actions = {
                            // Target-mode semantics (Gmail pattern): the icon shows the mode we
                            // will switch *to*, not the current one. Hidden while selecting.
                            IconToggleButton(
                                checked = isGrid,
                                onCheckedChange = { useGrid = it },
                            ) {
                                if (isGrid) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ViewList,
                                        contentDescription = "Switch to list view",
                                        modifier = Modifier.size(Dimens.IconSize),
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.GridView,
                                        contentDescription = "Switch to grid view",
                                        modifier = Modifier.size(Dimens.IconSize),
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Crossfade(targetState = isGrid, label = "categoryLayout") { grid ->
                when {
                    categoriesState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    categoriesState.categories.isEmpty() -> EmptyState(
                        icon = Icons.Outlined.QuestionAnswer,
                        title = "No Categories Saved Yet.",
                        description = "Download Qandas to save categories.",
                        fullSizeState = true,
                    )

                    else -> {
                        val categories = categoriesState.categories

                        if (grid) {
                            CategoryGrid(
                                categories = categories,
                                selectedIds = categoriesState.selectedIds,
                                inSelectionMode = selectionMode,
                                onItemClick = onItemClick,
                                onItemLongClick = onItemLongClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            CategoryList(
                                categories = categories,
                                selectedIds = categoriesState.selectedIds,
                                inSelectionMode = selectionMode,
                                onItemClick = onItemClick,
                                onItemLongClick = onItemLongClick,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }

            categoriesState.pendingDeletion?.let { pending ->
                DeleteCategoryDialog(
                    pending = pending,
                    onConfirm = viewmodel::confirmDeleteSelected,
                    onCancel = viewmodel::cancelDeletion,
                )
            }
        }
    }
}

/**
 * Confirmation dialog covering both single and bulk deletion. Wording branches on N
 * (categories being deleted) and M (distinct quizzes impacted):
 * N=1/M=0, N=1/M>0, N>1/M=0, N>1/M>0.
 */
@Composable
private fun DeleteCategoryDialog(
    pending: PendingCategoriesDeletion,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    val n = pending.categoryIds.size
    val m = pending.affectedQuizCount
    val quizWord = "quiz${if (m > 1) "zes" else ""}"
    val thoseQuizzes = if (m > 1) "those quizzes" else "that quiz"

    val title = if (n > 1) "Delete categories?" else "Delete category?"
    val message = when {
        n <= 1 && m == 0 ->
            "Delete \"${pending.categoryNames.firstOrNull().orEmpty()}\"? " +
                "This action cannot be undone."

        n <= 1 && m > 0 ->
            "\"${pending.categoryNames.firstOrNull().orEmpty()}\" is used in $m $quizWord. " +
                "Deleting it will also remove it from $thoseQuizzes. " +
                "This action cannot be undone."

        n > 1 && m == 0 ->
            "Delete these $n categories? This action cannot be undone."

        else -> // n > 1 && m > 0
            "These $n categories are used in $m $quizWord. " +
                "Deleting them will also remove them from $thoseQuizzes. " +
                "This action cannot be undone."
    }

    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun CategoryList(
    categories: List<DisplayableCategoryWithCount>,
    selectedIds: Set<String>,
    inSelectionMode: Boolean,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        // Reserve space so the last row is not hidden behind the floating nav capsule.
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            bottom = Dimens.BottomNavPadding,
        ),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryListItem(
                category = category,
                selected = category.id in selectedIds,
                inSelectionMode = inSelectionMode,
                onClick = { onItemClick(category.id) },
                onLongClick = { onItemLongClick(category.id) },
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp,
            )
        }
    }
}

@Composable
private fun CategoryListItem(
    category: DisplayableCategoryWithCount,
    selected: Boolean,
    inSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) = categoryAvatarColors(category.id)

    Box(
        // combinedClickable fires the long-press haptic by default — no manual call.
        modifier = modifier
            .fillMaxWidth()
            .then(if (selected) Modifier.selectedItemHighlight() else Modifier)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingMediumSmall),
        ) {
            CategoryAvatar(
                name = category.name,
                backgroundColor = backgroundColor,
                textColor = textColor,
                size = Dimens.CategoryAvatarSize,
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${category.questionsCount} questions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (inSelectionMode) {
            SelectionBadge(
                selected = selected,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = Dimens.PaddingSmall, top = Dimens.PaddingSmall),
            )
        }
    }
}

@Composable
private fun CategoryGrid(
    categories: List<DisplayableCategoryWithCount>,
    selectedIds: Set<String>,
    inSelectionMode: Boolean,
    onItemClick: (String) -> Unit,
    onItemLongClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        // Reserve extra bottom space so the last row clears the floating nav capsule.
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = Dimens.PaddingMedium,
            end = Dimens.PaddingMedium,
            top = Dimens.PaddingMedium,
            bottom = Dimens.BottomNavPadding,
        ),
        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
    ) {
        items(categories, key = { it.id }) { category ->
            CategoryGridItem(
                category = category,
                selected = category.id in selectedIds,
                inSelectionMode = inSelectionMode,
                onClick = { onItemClick(category.id) },
                onLongClick = { onItemLongClick(category.id) },
            )
        }
    }
}

@Composable
private fun CategoryGridItem(
    category: DisplayableCategoryWithCount,
    selected: Boolean,
    inSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) = categoryAvatarColors(category.id)
    val avatarGridSize = Dimens.CategoryAvatarSize * 2

    // Box overlay so the selection badge floats over the centered Column without
    // disturbing its layout. combinedClickable fires the long-press haptic by default.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .then(if (selected) Modifier.selectedItemHighlight(MaterialTheme.shapes.medium) else Modifier)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.PaddingMedium, horizontal = Dimens.PaddingSmall),
        ) {
            CategoryAvatar(
                name = category.name,
                backgroundColor = backgroundColor,
                textColor = textColor,
                size = avatarGridSize,
            )

            Spacer(modifier = Modifier.width(Dimens.PaddingSmall))

            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.PaddingSmall),
            )
            Text(
                text = "${category.questionsCount} questions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }

        if (inSelectionMode) {
            SelectionBadge(
                selected = selected,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = Dimens.PaddingSmall, top = Dimens.PaddingSmall),
            )
        }
    }
}

/**
 * Selected-item highlight shared by list and grid: a subtle primary-tinted fill plus a
 * 2dp primary ring. The 2dp ring is a semantic border literal (per the "Dimens Sweep"
 * convention that border widths are not tokenized). [shape] lets the grid round its
 * corners; the list passes the default rectangle.
 */
@Composable
private fun Modifier.selectedItemHighlight(
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.ui.graphics.RectangleShape,
): Modifier = this
    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), shape)
    .border(2.dp, MaterialTheme.colorScheme.primary, shape)

/**
 * Multi-select corner badge: a filled primary check when [selected], otherwise an empty
 * outline ring. Both sit on a subtle surface backing so they read over any avatar colour.
 */
@Composable
private fun SelectionBadge(
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    if (selected) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(Dimens.SelectionBadgeSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(Dimens.SelectionBadgeSize * 0.65f),
            )
        }
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(Dimens.SelectionBadgeSize)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
        ) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Not selected",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimens.SelectionBadgeSize),
            )
        }
    }
}

@Composable
private fun CategoryAvatar(
    name: String,
    backgroundColor: Color,
    textColor: Color,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

@Composable
private fun categoryAvatarColors(categoryId: String): Pair<Color, Color> {
    val colorIndex = abs(categoryId.hashCode()) % 3
    val backgroundColor = when (colorIndex) {
        0 -> MaterialTheme.colorScheme.primaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.tertiaryContainer
    }
    val textColor = when (colorIndex) {
        0 -> MaterialTheme.colorScheme.onPrimaryContainer
        1 -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onTertiaryContainer
    }
    return backgroundColor to textColor
}
