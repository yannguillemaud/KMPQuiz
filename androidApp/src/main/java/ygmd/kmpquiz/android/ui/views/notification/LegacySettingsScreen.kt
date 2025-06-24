package ygmd.kmpquiz.android.ui.views.notification

import androidx.compose.runtime.Composable
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel

@Composable
fun LegacyNotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
//    val uiState: NotificationCronSettings by viewModel.notificationConfigState.collectAsState()
//
//    val config = uiState.config
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFF8F9FA))
//            .statusBarsPadding()
//    ) {
//        Column(modifier = Modifier.fillMaxSize()) {
//            // Header
//            NotificationSettingsHeader(onNavigateBack = onNavigateBack)
//
//            LazyColumn(
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth(),
//                contentPadding = PaddingValues(24.dp),
//                verticalArrangement = Arrangement.spacedBy(20.dp)
//            ) {
//                // Configuration globale
//                item {
//                    GlobalNotificationCard(
//                        isEnabled = config.isEnabled,
//                        globalCron = config.globalCron,
//                        onToggleEnabled = viewModel::toggleGlobalNotifications,
//                        onUpdateCron = viewModel::updateGlobalCron
//                    )
//                }
//
//                if(config.isEnabled) {
//                    // Configurations par catégorie
//                    if(config.cronsByCategory.isEmpty()){
//                        item {
//                            Text(
//                                text = "Aucune notification définie",
//                                style = TextStyle(
//                                    fontSize = 18.sp,
//                                    fontWeight = FontWeight.SemiBold,
//                                    color = Color(0xFF1F2937)
//                                )
//                            )
//
//                        }
//                    }
//                    else {
//                        with(config.cronsByCategory) {
//                            item {
//                                Text(
//                                    text = "Notifications par catégorie",
//                                    style = TextStyle(
//                                        fontSize = 18.sp,
//                                        fontWeight = FontWeight.SemiBold,
//                                        color = Color(0xFF1F2937)
//                                    )
//                                )
//                            }
//
//                            items(keys.toList()) { category ->
//                                CategoryNotificationCard(
//                                    category = category,
//                                    config = getValue(category),
//                                    onSetCron = { cronPreset ->
//                                        viewModel.setCategoryCron(
//                                            category,
//                                            cronPreset
//                                        )
//                                    },
//                                    onRemove = { viewModel.removeCategoryCron(category) }
//                                )
//                            }
//                        }
//                    }
//                }
//            }
//
//            // Footer avec bouton d'application
//            Card(
//                modifier = Modifier.fillMaxWidth(),
//                colors = CardDefaults.cardColors(containerColor = Color.White),
//                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(24.dp),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    OutlinedButton(
//                        onClick = viewModel::reset,
//                        modifier = Modifier.weight(1f)
//                    ) {
//                        Text("Réinitialiser")
//                    }
//                }
//            }
//        }
//    }
}
