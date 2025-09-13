package ygmd.kmpquiz.android.ui.views.quiz

//@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Composable
//fun QuizCreationScreen(
//    onSavedQuiz: () -> Unit,
//    onCancelCreation: () -> Unit,
//    quizViewModel: QuizViewModel = koinViewModel(),
//) {
//    val availableCategories = quizViewModel.qandas.collectAsState(emptyList())
//
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//    var selectedQandas by remember { mutableStateOf(listOf<Qanda>()) }
//    var selectedCron: QuizCron? by remember { mutableStateOf(null) }
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    fun tryQuizCreation() {
//        quizViewModel.processIntent(
//            QuizIntent.CreateQuiz(
//                title = title,
//                qandas = selectedQandas,
//                cronSetting = selectedCron
//            )
//        )
//        onSavedQuiz()
//    }
//
//    LaunchedEffect(Unit) {
//        quizViewModel.quizEvents.collect { event ->
//            val result = when (event) {
//                is UiEvent.Success -> snackbarHostState.showSnackbar(event.message)
//                is UiEvent.Error -> snackbarHostState.showSnackbar(
//                    message = event.message,
//                    actionLabel = event.action?.label,
//                )
//            }
//        }
//    }
//
//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(hostState = snackbarHostState)
//        },
//        topBar = {
//            TopAppBar(
//                title = { Text("Créer un quiz") },
//                navigationIcon = {
//                    IconButton(onClick = onCancelCreation) {
//                        Icon(Icons.Default.Close, contentDescription = "Annuler")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            OutlinedTextField(
//                value = title,
//                onValueChange = { title = it },
//                label = { Text("Titre du quiz") },
//                modifier = Modifier.fillMaxWidth(),
//                isError = title.isBlank() && title.isNotEmpty()
//            )
//            OutlinedTextField(
//                value = description,
//                onValueChange = { description = it },
//                label = { Text("Description") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            SelectableCategories(
//                questionsByCategories = availableCategories.value
//                    .groupBy { it.metadata.category },
//                onSelectQandas = { selectedQandas = it }
//            )
//            SelectableCron(
//                onSelectCron = { selectedCron = it }
//            )
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Button(
//                onClick = {
//                    tryQuizCreation()
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Enregistrer")
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SelectableCategories(
//    questionsByCategories: Map<String, List<Qanda>>,
//    onSelectQandas: (List<Qanda>) -> Unit,
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
//
//    // Texte affiché dans le champ (résumé des sélections)
//    val displayText =
//        if (selectedCategories.isEmpty()) "Aucune sélection"
//        else selectedCategories.joinToString()
//
//    ExposedDropdownMenuBox(
//        expanded = isExpanded,
//        onExpandedChange = { isExpanded = it }
//    ) {
//        OutlinedTextField(
//            value = displayText,
//            onValueChange = {},
//            readOnly = true,
//            modifier = Modifier
//                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
//                .fillMaxWidth(),
//            label = { Text("Catégories") },
//            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
//            colors = OutlinedTextFieldDefaults.colors(
//                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
//                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
//                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
//            )
//        )
//
//        ExposedDropdownMenu(
//            expanded = isExpanded,
//            // 👇 on laisse le menu ouvert pour multi-select
//            onDismissRequest = { isExpanded = false },
//            modifier = Modifier.exposedDropdownSize()
//        ) {
//            Column(Modifier.padding(8.dp)) {
//                questionsByCategories.forEach { (categoryName, qandas) ->
//                    val isSelected = categoryName in selectedCategories
//                    FilterChip(
//                        selected = isSelected,
//                        onClick = {
//                            selectedCategories =
//                                if (isSelected) selectedCategories - categoryName
//                                else selectedCategories + categoryName
//                            onSelectQandas(qandas)
//                        },
//                        label = { Text(categoryName) },
//                        modifier = Modifier.padding(vertical = 4.dp)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SelectableCron(
//    onSelectCron: (QuizCron) -> Unit,
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    var cronDisplayName by remember { mutableStateOf("") }
//
//    ExposedDropdownMenuBox(
//        expanded = isExpanded,
//        onExpandedChange = { isExpanded = it }
//    ) {
//        OutlinedTextField(
//            value = cronDisplayName,
//            onValueChange = {},
//            readOnly = true,
//            modifier = Modifier
//                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
//                .fillMaxWidth(),
//            label = { Text("Catégorie") },
//            trailingIcon = {
//                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
//            },
//            colors = OutlinedTextFieldDefaults.colors(
//                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
//                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
//                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
//            )
//        )
//
//        ExposedDropdownMenu(
//            expanded = isExpanded,
//            onDismissRequest = { isExpanded = false },
//            modifier = Modifier.exposedDropdownSize()
//        ) {
//            QuizCronPreset.entries.forEach {
//                DropdownMenuItem(
//                    text = {
//                        Text(
//                            text = it.displayName,
//                            style = typography.bodyMedium
//                        )
//                    },
//                    onClick = {
//                        isExpanded = false
//                        cronDisplayName = it.displayName
//                        onSelectCron(QuizCron(it.toCronExpression()))
//                    }
//                )
//            }
//        }
//    }
//}