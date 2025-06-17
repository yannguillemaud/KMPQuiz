package ygmd.kmpquiz.android.ui.views.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.CategoriesSection
import ygmd.kmpquiz.android.ui.composable.StatsCard
import ygmd.kmpquiz.domain.entities.qanda.Qanda
import ygmd.kmpquiz.viewModel.save.SavedQandasUiState
import ygmd.kmpquiz.viewModel.save.SavedQandasViewModel

@Composable
fun SavedScreen(
    viewModel: SavedQandasViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onStartQuiz: (List<Long>) -> Unit = {}
) {
    val uiState by viewModel.savedState.collectAsState()
    var isGridView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearchBar by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0xFFF8F9FA))
    ) {
        // Header épuré
        TopBar(
            isGridView = isGridView,
            showSearchBar = showSearchBar,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onToggleSearch = { showSearchBar = !showSearchBar },
            onToggleView = { isGridView = !isGridView },
            onNavigateBack = onNavigateBack,
            totalQuizCount = when (val state = uiState) {
                is SavedQandasUiState.Success -> state.qandas.size
                else -> 0
            }
        )

        when (val state = uiState) {
            is SavedQandasUiState.Success -> {
                if (state.qandas.isEmpty()) {
                    MinimalEmptyStateSection(
                        onExploreQuiz = { /* TODO: Navigate to fetch */ }
                    )
                } else {
                    // Filtrage
                    val filteredQandas = state.qandas.filter { qanda ->
                        val matchesSearch =
                            if (searchQuery.isBlank()) true
                            else {
                                qanda.question.contains(searchQuery, ignoreCase = true)
                                        || qanda.category.contains(searchQuery, ignoreCase = true)
                            }
                        val matchesCategory = selectedCategory?.let { category ->
                            qanda.category == category
                        } ?: true
                        matchesSearch && matchesCategory
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(24.dp, vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Filtres catégories
                                item {
                                    CategoriesSection(
                                        categories = state.categories,
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = {
                                            selectedCategory =
                                                if (selectedCategory == it) null else it
                                        }
                                    )
                                }

                                // Liste des quiz
                                items(filteredQandas) { qanda ->
                                    QuizCard(
                                        qanda = qanda,
                                        onClick = {
                                            println("Quiz: $qanda")
                                            onStartQuiz(listOfNotNull(qanda.id))
                                        },
                                        onDelete = { viewModel.deleteQanda(qanda) },
                                        onFavoriteToggle = { viewModel.toggleFavorite(qanda) }
                                    )
                                }
                            }
                        }

                        // Statistiques compactes
                        if (!showSearchBar && selectedCategory == null) {
                            StatsCard(
                                modifier = Modifier
                                    .align(alignment = Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                totalQuiz = state.qandas.size,
                                categories = state.categories.size,
                                // TODO
                                favorites = 0,
                                onStartRandomQuiz = {
                                    val qandas = state.qandas.shuffled().take(1)
                                    val ids = qandas.mapNotNull { it.id }
                                    onStartQuiz(ids)
                                },
                            )
                        }
                    }
                }
            }

            else -> {
                MinimalErrorSection(
                    error = "Error when retrieving saved qandas",
                    onRetry = {
                        // TODO: viewModel.loadSavedQandas()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    isGridView: Boolean,
    showSearchBar: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleSearch: () -> Unit,
    onToggleView: () -> Unit,
    onNavigateBack: () -> Unit,
    totalQuizCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header principal
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color(0xFF1F2937)
                    )
                }

                if (!showSearchBar) {
                    Column {
                        Text(
                            text = "Mes Quiz",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1F2937)
                            )
                        )
                        if (totalQuizCount > 0) {
                            Text(
                                text = "$totalQuizCount quiz sauvegardés",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFF6B7280)
                                )
                            )
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onToggleSearch,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (showSearchBar) Icons.Filled.Close else Icons.Filled.Search,
                        contentDescription = if (showSearchBar) "Fermer" else "Rechercher",
                        tint = Color(0xFF1F2937)
                    )
                }

                if (totalQuizCount > 0) {
                    IconButton(
                        onClick = onToggleView,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Filled.GridView,
                            contentDescription = if (isGridView) "Liste" else "Grille",
                            tint = Color(0xFF1F2937)
                        )
                    }
                }
            }
        }

        // Barre de recherche
        AnimatedVisibility(
            visible = showSearchBar,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        "Rechercher un quiz...",
                        color = Color(0xFF9CA3AF)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Effacer",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF4F46E5),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )
        }
    }
}


@Composable
fun MinimalStatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F46E5)
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color(0xFF6B7280)
            )
        )
    }
}

@Composable
fun QuizCard(
    qanda: Qanda,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header avec catégorie et actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (qanda.difficulty.lowercase()) {
                                    "easy" -> Color(0xFF10B981)
                                    "medium" -> Color(0xFFF59E0B)
                                    "hard" -> Color(0xFFEF4444)
                                    else -> Color(0xFF6B7280)
                                },
                                CircleShape
                            )
                    )

                    Text(
                        text = qanda.category,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF6B7280)
                        )
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder, // TODO: Gérer l'état favori
                            contentDescription = "Favori",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6B7280)
                        )
                    }

                    // TODO: Ajouter le bouton delete si nécessaire
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question
            Text(
                text = qanda.question,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937),
                    lineHeight = 22.sp
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Footer avec difficulté
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = qanda.difficulty.lowercase().replaceFirstChar { it.uppercase() },
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (qanda.difficulty.lowercase()) {
                            "easy" -> Color(0xFF10B981)
                            "medium" -> Color(0xFFF59E0B)
                            "hard" -> Color(0xFFEF4444)
                            else -> Color(0xFF6B7280)
                        }
                    )
                )

                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Jouer",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF4F46E5)
                )
            }
        }
    }
}

@Composable
fun MinimalLoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color(0xFF4F46E5),
                strokeWidth = 3.dp
            )
            Text(
                text = "Chargement des quiz...",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                )
            )
        }
    }
}

@Composable
fun MinimalEmptyStateSection(
    onExploreQuiz: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFF3F4F6),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF9CA3AF)
                )
            }

            Text(
                text = "Aucun quiz sauvegardé",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Découvrez de nouveaux quiz et sauvegardez vos favoris",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onExploreQuiz,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Explore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Explorer les quiz",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun MinimalErrorSection(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Color(0xFFFEF2F2),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFFEF4444)
                )
            }

            Text(
                text = "Une erreur est survenue",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = error,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF6B7280)
                ),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4F46E5)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Réessayer",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}