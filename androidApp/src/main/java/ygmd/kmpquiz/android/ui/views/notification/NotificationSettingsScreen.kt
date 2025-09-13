package ygmd.kmpquiz.android.ui.views.notification

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationSettingsScreen(
//    onNavigateBack: () -> Unit,
//    viewModel: NotificationSettingsViewModel = koinViewModel()
//) {
//    val uiState by viewModel.cronsState.collectAsState(NotificationCronSettings())
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Paramètres de notifications",
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
//        containerColor = colorScheme.background
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            contentPadding = PaddingValues(16.dp),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            // Carte d'information en haut
//            item {
//                Card(
//                    modifier = Modifier.fillMaxWidth(),
//                    colors = CardDefaults.cardColors(
//                        containerColor = colorScheme.primaryContainer
//                    )
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Notifications,
//                            contentDescription = null,
//                            tint = colorScheme.onPrimaryContainer,
//                            modifier = Modifier.size(24.dp)
//                        )
//                        Spacer(modifier = Modifier.width(12.dp))
//                        Text(
//                            text = "Configuration des fréquences de notifications",
//                            style = typography.bodyMedium,
//                            color = colorScheme.onPrimaryContainer
//                        )
//                    }
//                }
//            }
//
//            // Liste des paramètres
//            val cronsByCategory = uiState.cronsByQuiz
//            items(
//                items = cronsByCategory.entries.toList(),
//                key = { it.key.id }
//            ) { (quiz, cronSetting) ->
//                CronSettingCard(
//                    cronSetting = cronSetting,
//                    onToggleCron = {
//                        viewModel.dispatchIntent(NotificationSettingsIntent.UpdateCron(quiz, it))
//                    },
//                    onUpdateCron = {
//                        viewModel.dispatchIntent(
//                            NotificationSettingsIntent.UpdateCron(quiz, it)
//                        )
//                    },
//                    onDeleteCron = {
//                        viewModel.dispatchIntent(
//                            NotificationSettingsIntent.DeleteCron(quiz)
//                        )
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun CronSettingCard(
//    cronSetting: QuizCron,
//    onToggleCron: (QuizCron) -> Unit,
//    onUpdateCron: (QuizCron) -> Unit,
//    onDeleteCron: (QuizCron) -> Unit,
//) {
//    val title = cronSetting.cron.displayName
//    val isEnabled = cronSetting.isEnabled
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .semantics { contentDescription = "Paramètre pour $title" },
//        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // En-tête avec titre et switch
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = title,
//                    style = typography.titleMedium,
//                    fontWeight = FontWeight.Medium,
//                    color = colorScheme.onSurface
//                )
//
//                Switch(
//                    checked = isEnabled,
//                    onCheckedChange = { onToggleCron(cronSetting) },
//                    modifier = Modifier.semantics {
//                        contentDescription = if (isEnabled)
//                            "Désactiver les notifications pour $title"
//                        else "Activer les notifications pour $title"
//                    }
//                )
//
//                Button(onClick = { onDeleteCron(cronSetting)}){
//                    Text("-")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Section fréquence
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Schedule,
//                    contentDescription = null,
//                    tint = if (isEnabled) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.38f),
//                    modifier = Modifier.size(20.dp)
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                CronSettingChoice(
//                    modifier = Modifier.weight(1f),
//                    setting = cronSetting,
//                    onUpdateCron = onUpdateCron
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CronSettingChoice(
//    modifier: Modifier = Modifier,
//    setting: QuizCron,
//    onUpdateCron: (QuizCron) -> Unit,
//) {
//    var isExpanded by remember { mutableStateOf(false) }
//    val isEnabled = setting.isEnabled
//    val displayName = setting.cron.displayName
//
//    ExposedDropdownMenuBox(
//        expanded = isExpanded,
//        onExpandedChange = { isExpanded = isEnabled && it },
//        modifier = modifier
//    ) {
//        OutlinedTextField(
//            value = displayName,
//            onValueChange = {},
//            readOnly = true,
//            enabled = isEnabled,
//            modifier = Modifier
//                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
//                .fillMaxWidth()
//                .semantics {
//                    contentDescription = "Fréquence sélectionnée: $displayName"
//                },
//            label = { Text("Fréquence") },
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
//            QuizCronPreset.entries.forEach { preset ->
//                DropdownMenuItem(
//                    text = {
//                        Text(
//                            text = preset.displayName,
//                            style = typography.bodyMedium
//                        )
//                    },
//                    onClick = {
//                        isExpanded = false
//                        onUpdateCron(setting.copy(cron = preset.toCronExpression()))
//                    },
//                    modifier = Modifier.semantics {
//                        contentDescription = "Sélectionner ${preset.displayName}"
//                    }
//                )
//            }
//        }
//    }
//}