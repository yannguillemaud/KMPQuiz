package ygmd.kmpquiz.android.ui.composable

// Écran des statistiques détaillées
/*
@Composable
fun StatisticsScreen(
    stats: QuizStats, onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour"
                )
            }
            Text(
                text = "Statistiques", style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ), modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statistiques générales
            item {
                StatisticCard(
                    title = "Vue d'ensemble", content = {
                        Column {
                            StatRow("Total des quiz", "${stats.totalQuiz}")
                            StatRow("Catégories", "${stats.categoriesCount}")
                            StatRow("Favoris", "${stats.favoritesCount}")
                        }
                    })
            }

            // Répartition par difficulté
            item {
                StatisticCard(
                    title = "Répartition par difficulté", content = {
                        Column {
                            stats.difficultyBreakdown.forEach { (difficulty, count) ->
                                val percentage = if (stats.totalQuiz > 0) {
                                    (count * 100f / stats.totalQuiz).toInt()
                                } else 0

//                                DifficultyStatRow(
//                                    difficulty = difficulty, count = count, percentage = percentage
//                                )
                            }
                        }
                    })
            }

            // Quiz récemment ajoutés
            if (stats.recentlyAdded.isNotEmpty()) {
                item {
                    StatisticCard(
                        title = "Ajoutés récemment", content = {
                            Column {
                                stats.recentlyAdded.take(3).forEach { qanda ->
                                    RecentQuizItem(qanda = qanda)
                                }
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String, content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title, style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ), color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value, style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ), color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun RecentQuizItem(qanda: InternalQanda) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
//        DifficultyIndicator(difficulty = qanda.difficulty)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = qanda.question,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = qanda.category,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

// Extensions pour améliorer l'UX
@Composable
fun LazyRow(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}*/
