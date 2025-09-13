package ygmd.kmpquiz.android.ui.views.fetch.success

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FetchScreen(
//    onNavigateBack: () -> Unit = {},
//    coordinator: FetchScreenCoordinator = koinViewModel(),
//    fetchQandasViewModel: FetchQandasViewModel = koinViewModel(),
//    savedQandasViewModel: SavedQandasViewModel = koinViewModel(),
//) {
//    val state by coordinator.state.collectAsState(FetchScreenState())
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    LaunchedEffect(Unit) {
//        merge(
//            fetchQandasViewModel.events,
//            savedQandasViewModel.events
//        ).collect { event ->
//            val result = when (event) {
//                is UiEvent.Success -> {}
//                is UiEvent.Error -> snackbarHostState.showSnackbar(
//                    event.message,
//                    actionLabel = event.action?.label
//                )
//            }
//            when (result) {
//                SnackbarResult.ActionPerformed -> {
//                    fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch)
//                }
//
//                SnackbarResult.Dismissed -> {}
//            }
//        }
//    }
//    Scaffold(
//        snackbarHost = {
//            SnackbarHost(hostState = snackbarHostState)
//        },
//        floatingActionButton = {
//            FloatingActionButton(onClick = { fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch) }) {
//                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Refresh")
//            }
//        },
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Fetch",
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
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color(0xFFF8F9FA))
//                .padding(paddingValues)
//        ) {
//            val qandasByCategory = state.qandasByCategory
//            val categories = qandasByCategory.keys
//            Button(
//                onClick = { fetchQandasViewModel.onIntentAction(FetchIntentAction.Fetch) },
//                enabled = state.canRefresh
//            ) {
//                Text("Refresh")
//            }
//            LazyColumn {
//                items(categories.toList()) {
//                    val groupedQandasState = qandasByCategory[it]!!
//                    GroupedDraftQandasCard(
//                        identifier = it,
//                        qandas = groupedQandasState,
//                        onSaveAction = {
//                            savedQandasViewModel.processIntent(
//                                PersistanceIntent.SaveAll(groupedQandasState)
//                            )
//                        }
//                    )
//                }
//            }
//        }
//    }
//}