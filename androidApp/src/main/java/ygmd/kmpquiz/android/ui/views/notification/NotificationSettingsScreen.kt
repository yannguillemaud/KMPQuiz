package ygmd.kmpquiz.android.ui.views.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.android.ui.composable.notificationsetting.CronSelector
import ygmd.kmpquiz.domain.entities.cron.CronExpression
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.viewModel.settings.NotificationCronSettings
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel

@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit, viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val uiState: State<NotificationCronSettings> = viewModel.notificationUiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            NotificationSettingsHeader { onNavigateBack() }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    GlobalNotificationCard(
                        isEnabled = uiState.value.globalCron?.isActive == true,
                        globalCron = uiState.value.globalCron?.expression,
                        onToggleEnabled = {
                            uiState.value.globalCron?.run {
                                viewModel.updateGlobalCron(this.copy(isActive = it))
                            }
                        },
                        onUpdateCron = {
                            uiState.value.globalCron?.run {
                                viewModel.updateGlobalCron(this.copy(expression = it.toCronExpression()))
                            }
                        })
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
            onClick = onNavigateBack, modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour",
                tint = Color(0xFF1F2937)
            )
        }

        Column {
            Text(
                text = "Notifications", style = TextStyle(
                    fontSize = 24.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937)
                )
            )
            Text(
                text = "Configurez vos rappels de quiz", style = TextStyle(
                    fontSize = 14.sp, color = Color(0xFF6B7280)
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
            modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            text = "Notifications globales", style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937)
                            )
                        )
                        Text(
                            text = if (isEnabled) "Activées" else "Désactivées", style = TextStyle(
                                fontSize = 12.sp, color = Color(0xFF6B7280)
                            )
                        )
                    }
                }

                Switch(
                    checked = isEnabled, onCheckedChange = onToggleEnabled
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