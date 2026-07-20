package ygmd.kmpquiz.presentation.screen.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ygmd.kmpquiz.core.domain.qanda.QuestionContent
import ygmd.kmpquiz.presentation.composable.playquiz.ErrorState
import ygmd.kmpquiz.presentation.composable.qanda.QandaImagePreview
import ygmd.kmpquiz.presentation.theme.Dimens
import ygmd.kmpquiz.presentation.viewModel.category.CategoryQandaViewModel
import ygmd.kmpquiz.presentation.viewModel.category.CategoryQuestionsState
import ygmd.kmpquiz.presentation.viewModel.category.DisplayableQanda

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    categoryId: String,
    categoryQandaViewModel: CategoryQandaViewModel = koinViewModel(parameters = { parametersOf(categoryId) }),
    onNavigateBack: () -> Unit = {},
) {
    val categoryQandasState by categoryQandaViewModel.qandasUiState.collectAsStateWithLifecycle()
    val searchQuery by categoryQandaViewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var expandedImage by remember { mutableStateOf<Pair<String, String?>?>(null) }

    val stateType by remember {
        derivedStateOf {
            when (categoryQandasState) {
                is CategoryQuestionsState.Loading -> 0
                is CategoryQuestionsState.Error -> 1
                is CategoryQuestionsState.Success -> 2
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val titleText = when (val state = categoryQandasState) {
                        is CategoryQuestionsState.Success -> state.category.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(LocalLocale.current.platformLocale) else it.toString()
                        }
                        else -> "Questions"
                    }
                    Text(
                        text = titleText,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedVisibility(
                visible = categoryQandasState is CategoryQuestionsState.Success,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                QandaSearchBar(
                    query = searchQuery,
                    onQueryChange = categoryQandaViewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
                )
            }

            Crossfade(
                targetState = stateType,
                label = "CategoryScreenState",
                animationSpec = tween(300),
                modifier = Modifier.fillMaxSize()
            ) { type ->
                when (type) {
                    0 -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    1 -> {
                        val errorState = categoryQandasState as? CategoryQuestionsState.Error
                        ErrorState(
                            modifier = Modifier.fillMaxSize(),
                            message = errorState?.message ?: "Unknown error"
                        )
                    }

                    else -> {
                        val successState = categoryQandasState as? CategoryQuestionsState.Success
                        val qandas = successState?.qandas.orEmpty()
                        when {
                            qandas.isEmpty() && searchQuery.isNotEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No results for \"$searchQuery\"",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(Dimens.PaddingMedium)
                                    )
                                }
                            }

                            qandas.isEmpty() -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No questions in this category",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(Dimens.PaddingMedium)
                                    )
                                }
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(
                                        start = Dimens.PaddingMedium,
                                        end = Dimens.PaddingMedium,
                                        top = Dimens.PaddingSmall,
                                        // Clear the floating nav capsule at the bottom.
                                        bottom = Dimens.BottomNavPadding,
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMediumSmall)
                                ) {
                                    items(qandas, key = { it.id }) { qanda ->
                                        QandaCard(
                                            qanda,
                                            onImageClick = { url, altText -> expandedImage = url to altText }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    expandedImage?.let { (url, altText) ->
        QandaImagePreview(
            imageUrl = url,
            altText = altText,
            onDismiss = { expandedImage = null }
        )
    }
}

@Composable
private fun QandaSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search questions...") },
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {}),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
private fun QandaCard(qanda: DisplayableQanda, onImageClick: (String, String?) -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        ListItem(
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            headlineContent = {
                when (val content = qanda.question) {
                    is QuestionContent.TextContent -> {
                        Text(
                            text = content.text,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    is QuestionContent.ImageContent -> {
                        Text(
                            text = content.altText ?: "Image question",
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            supportingContent = {
                val text = when (qanda.question) {
                    is QuestionContent.TextContent -> "Correct answer: ${qanda.answers.correctAnswer.contextKey}"
                    is QuestionContent.ImageContent -> "Tap image to preview"
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            leadingContent = {
                when (val content = qanda.question) {
                    is QuestionContent.ImageContent -> {
                        Box(contentAlignment = Alignment.TopEnd) {
                            AsyncImage(
                                model = content.imageUrl,
                                contentDescription = "Question image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(Dimens.QandaThumbnailSize)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable { onImageClick(content.imageUrl, content.altText) }
                            )
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(MaterialTheme.shapes.extraSmall)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = "Image question",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    is QuestionContent.TextContent -> {
                        Box(
                            modifier = Modifier.size(Dimens.QandaThumbnailSize),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        )
    }
}
