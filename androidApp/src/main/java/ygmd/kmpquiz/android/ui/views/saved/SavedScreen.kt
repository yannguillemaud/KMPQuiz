package ygmd.kmpquiz.android.ui.views.saved

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SavedScreen(
//    viewModel: SavedQandasViewModel = koinViewModel(),
//    onNavigateBack: () -> Unit = {},
//) {
//    val uiState by viewModel.saveState.collectAsState(SaveUiState())
//    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
//
//    val bottomSheetState = rememberBottomSheetScaffoldState()
//    val scope = rememberCoroutineScope()
//    var selectedQanda by remember { mutableStateOf<Qanda?>(null) }
//
//    BottomSheetScaffold(
//        scaffoldState = bottomSheetState,
//        sheetContent = {
//            selectedQanda?.let {
//                QandaDetailSheet(qanda = it)
//            } ?: run {
//                // contenu par défaut
//                Spacer(modifier = Modifier.height(1.dp))
//            }
//        },
//        sheetPeekHeight = 0.dp,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Qandas sauvegardés",
//                        style = typography.titleLarge
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Retour"
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = colorScheme.surface,
//                    titleContentColor = colorScheme.onSurface
//                )
//            )
//        },
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//            items(uiState.savedQandas.entries.toList()) { (category, qandas) ->
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                ) {
//                    SavedCategoryCard(
//                        category = category,
//                        qandas = qandas,
//                        isExpanded = category in expandedCategories,
//                        onToggleExpend = {
//                            expandedCategories = if (category !in expandedCategories) {
//                                expandedCategories + category
//                            } else {
//                                expandedCategories - category
//                            }
//                        },
//                        onDeleteCategory = {
//                            viewModel.processIntent(
//                                PersistanceIntent.DeleteByCategory(category)
//                            )
//                        },
//                        onDeleteQanda = { id ->
//                            viewModel.processIntent(
//                                PersistanceIntent.DeleteQanda(id)
//                            )
//                        },
//                        onQandaClick = { qanda ->
//                            selectedQanda = qanda
//                            scope.launch {
//                                bottomSheetState.bottomSheetState.expand()
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SavedCategoryCard(
//    category: String,
//    qandas: List<Qanda>,
//    isExpanded: Boolean,
//    onToggleExpend: () -> Unit,
//    onDeleteCategory: () -> Unit,
//    onDeleteQanda: (String) -> Unit,
//    onQandaClick: (Qanda) -> Unit,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//    ) {
//        // En-tête de la catégorie
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { onToggleExpend() },
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically,
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    text = category,
//                    style = typography.titleMedium
//                )
//                Text(
//                    text = "${qandas.size} questions",
//                    style = typography.bodySmall,
//                    color = colorScheme.onSurfaceVariant
//                )
//            }
//
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                IconButton(onClick = onDeleteCategory) {
//                    Icon(
//                        imageVector = Icons.Default.Delete,
//                        contentDescription = "Supprimer catégorie",
//                        tint = colorScheme.error
//                    )
//                }
//                Icon(
//                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
//                    contentDescription = if (isExpanded) "Réduire" else "Afficher"
//                )
//            }
//        }
//
//        // Liste des questions (si expandé)
//        if (isExpanded) {
//            Spacer(modifier = Modifier.height(12.dp))
//            qandas.forEach { qanda ->
//                QandaItem(
//                    qanda = qanda,
//                    onDelete = { onDeleteQanda(qanda.id.toString()) },
//                    onClick = { onQandaClick(qanda) }
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//            }
//        }
//    }
//}
//
//@Composable
//private fun QandaItem(
//    qanda: Qanda,
//    onDelete: () -> Unit,
//    onClick: () -> Unit,
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .pointerInput(Unit){
//                detectTapGestures(
//                    onLongPress = { onClick() }
//                )
//            },
//        colors = CardDefaults.cardColors(
//            containerColor = colorScheme.surfaceVariant
//        )
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
////            Text(
////                text = qanda.question.text,
////                style = typography.bodyMedium,
////                modifier = Modifier.weight(1f)
////            )
//
//            IconButton(onClick = onDelete) {
//                Icon(
//                    imageVector = Icons.Default.Delete,
//                    contentDescription = "Supprimer",
//                    tint = colorScheme.error
//                )
//            }
//        }
//    }
//}


//@Composable
//private fun QandaDetailSheet(
//    qanda: Qanda
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(24.dp)
//    ) {
//        // Header avec titre
//        Text(
//            text = "Détails de la question",
//            style = typography.titleLarge,
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        // Question
//        Text(
//            text = "Question",
//            style = typography.titleMedium,
//            fontWeight = FontWeight.Bold,
//            color = colorScheme.primary
//        )
////        Text(
////            text = qanda.question.text,
////            style = typography.bodyLarge,
////            modifier = Modifier.padding(bottom = 16.dp)
////        )
//
//        // Réponses
//        Text(
//            text = "Réponses",
//            style = typography.titleMedium,
//            fontWeight = FontWeight.Bold,
//            color = colorScheme.primary,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//
//        qanda.answers.choices.forEach { choice ->
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                // Indicateur correct/incorrect
//                Text(
//                    text = if (choice == qanda.correctAnswer) "✅" else "❌",
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//
//                // Texte de la réponse
//                Text(
//                    text = when (choice) {
//                        is Choice.TextChoice -> choice.text
//                        is Choice.ImageChoice -> "Image: ${choice.altText ?: "Sans description"}"
//                    },
//                    style = if (choice == qanda.correctAnswer) {
//                        typography.bodyMedium.copy(
//                            fontWeight = FontWeight.Bold,
//                            color = colorScheme.primary
//                        )
//                    } else {
//                        typography.bodyMedium
//                    }
//                )
//            }
//        }
//
//        // Métadonnées
//        qanda.metadata.category.let { category ->
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = "Catégorie: $category",
//                style = typography.bodySmall,
//                color = colorScheme.onSurfaceVariant
//            )
//        }
//
//        qanda.metadata.difficulty?.let { difficulty ->
//            Text(
//                text = "Difficulté: $difficulty",
//                style = typography.bodySmall,
//                color = colorScheme.onSurfaceVariant
//            )
//        }
//    }
//}