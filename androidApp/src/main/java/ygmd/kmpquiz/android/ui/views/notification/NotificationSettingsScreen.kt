package ygmd.kmpquiz.android.ui.views.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.domain.entities.notification.CategoryNotificationConfig
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            NotificationSettingsHeader(onNavigateBack = onNavigateBack)

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Configuration globale
                item {
                    GlobalNotificationCard(
                        isEnabled = uiState.config.isEnabled,
                        globalCron = uiState.config.globalCron,
                        onToggleEnabled = viewModel::toggleGlobalNotifications,
                        onUpdateCron = viewModel::updateGlobalCron
                    )
                }

                // Configurations par catégorie
                item {
                    Text(
                        text = "Notifications par catégorie",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1F2937)
                        )
                    )
                }

                items(uiState.availableCategories) { category ->
                    val categoryConfig = uiState.config.categoryCrons[category]
                    CategoryNotificationCard(
                        category = category,
                        config = categoryConfig,
                        onSetCron = { cronPreset -> viewModel.setCategoryCron(category, cronPreset) },
                        onRemove = { viewModel.removeCategoryCron(category) }
                    )
                }
            }

            // Footer avec bouton d'application
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: Reset to defaults */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Réinitialiser")
                    }

                    Button(
                        onClick = viewModel::applyChanges,
                        enabled = !isLoading,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Appliquer les changements")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsHeader(
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
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

        Column {
            Text(
                text = "Notifications",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
            )
            Text(
                text = "Configurez vos rappels de quiz",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            )
        }
    }
}

@Composable
private fun GlobalNotificationCard(
    isEnabled: Boolean,
    globalCron: CronExpression?,
    onToggleEnabled: (Boolean) -> Unit,
    onUpdateCron: (CronPreset) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (isEnabled) Icons.Filled.Notifications else Icons.Filled.NotificationsOff,
                        contentDescription = null,
                        tint = if (isEnabled) Color(0xFF4F46E5) else Color(0xFF9CA3AF),
                        modifier = Modifier.size(24.dp)
                    )

                    Column {
                        Text(
                            text = "Notifications globales",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937)
                            )
                        )
                        Text(
                            text = if (isEnabled) "Activées" else "Désactivées",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled
                )
            }

            AnimatedVisibility(visible = isEnabled) {
                CronSelector(
                    selectedCron = globalCron,
                    onCronSelected = onUpdateCron,
                    label = "Fréquence par défaut"
                )
            }
        }
    }
}

@Composable
private fun CategoryNotificationCard(
    category: String,
    config: CategoryNotificationConfig?,
    onSetCron: (CronPreset) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (config != null) Color(0xFFF0F0FF) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = category,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937)
                        )
                    )
                    Text(
                        text = if (config != null) "Configuration personnalisée" else "Utilise la configuration globale",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    )
                }

                if (config != null) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (config == null) {
                OutlinedButton(
                    onClick = { onSetCron(CronPreset.DAILY) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Personnaliser cette catégorie")
                }
            } else {
                CronSelector(
                    selectedCron = config.cronExpression,
                    onCronSelected = onSetCron,
                    label = "Fréquence pour cette catégorie"
                )
            }
        }
    }
}

@Composable
private fun CronSelector(
    selectedCron: CronExpression?,
    onCronSelected: (CronPreset) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF374151)
            )
        )

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = selectedCron?.let { cronToDisplayText(it) } ?: "Sélectionner une fréquence"
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                CronPreset.entries.forEach { preset ->
                    DropdownMenuItem(
                        text = { Text(cronPresetToDisplayText(preset)) },
                        onClick = {
                            onCronSelected(preset)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun cronToDisplayText(cron: CronExpression): String {
    return when (cron.toString()) {
        "0 0 * * *" -> "Tous les jours"
        "0 0 * * 0" -> "Toutes les semaines"
        "0 0 1 * *" -> "Tous les mois"
        "0 0 1 1 *" -> "Tous les ans"
        "0 * * * *" -> "Toutes les heures"
        else -> "Personnalisé"
    }
}

private fun cronPresetToDisplayText(preset: CronPreset): String {
    return when (preset) {
        CronPreset.DAILY -> "Tous les jours"
        CronPreset.WEEKLY -> "Toutes les semaines"
        CronPreset.MONTHLY -> "Tous les mois"
        CronPreset.YEARLY -> "Tous les ans"
        CronPreset.HOURLY -> "Toutes les heures"
        CronPreset.EVERY_MINUTE -> "Toutes les minutes (test)"
    }
}