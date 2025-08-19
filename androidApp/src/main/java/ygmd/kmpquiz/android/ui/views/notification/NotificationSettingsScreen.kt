package ygmd.kmpquiz.android.ui.views.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import ygmd.kmpquiz.domain.entities.cron.CronPreset
import ygmd.kmpquiz.viewModel.settings.UiCronSetting
import ygmd.kmpquiz.viewModel.settings.NotificationCronSettings
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsIntent
import ygmd.kmpquiz.viewModel.settings.NotificationSettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.cronSettingsState.collectAsState(NotificationCronSettings())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Paramètres de notifications",
                        style = typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.surface,
                    titleContentColor = colorScheme.onSurface
                )
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Carte d'information en haut
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Configuration des fréquences de notifications",
                            style = typography.bodyMedium,
                            color = colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Liste des paramètres
            val cronsByCategory = uiState.cronsByQuiz
            items(
                items = cronsByCategory.entries.toList(),
                key = { it.key.id }
            ) { (quiz, cronSetting) ->
                CronSettingCard(
                    cronSetting = cronSetting,
                    onToggleCron = {
                        viewModel.dispatchIntent(
                            NotificationSettingsIntent.ToggleCron(quiz, it)
                        )
                    },
                    onUpdateCron = {
                        viewModel.dispatchIntent(
                            NotificationSettingsIntent.UpdateCron(quiz, it)
                        )
                    },
                    onDeleteCron = {
                        viewModel.dispatchIntent(
                            NotificationSettingsIntent.DeleteCron(quiz)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun CronSettingCard(
    cronSetting: UiCronSetting,
    onToggleCron: (UiCronSetting) -> Unit,
    onUpdateCron: (UiCronSetting) -> Unit,
    onDeleteCron: (UiCronSetting) -> Unit,
) {
    val title = cronSetting.title
    val isEnabled = cronSetting.isEnabled

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Paramètre pour $title" },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // En-tête avec titre et switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggleCron(cronSetting) },
                    modifier = Modifier.semantics {
                        contentDescription = if (isEnabled)
                            "Désactiver les notifications pour $title"
                        else "Activer les notifications pour $title"
                    }
                )

                Button(onClick = { onDeleteCron(cronSetting)}){
                    Text("-")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Section fréquence
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isEnabled) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.38f),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                CronSettingChoice(
                    modifier = Modifier.weight(1f),
                    setting = cronSetting,
                    onUpdateCron = onUpdateCron
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CronSettingChoice(
    modifier: Modifier = Modifier,
    setting: UiCronSetting,
    onUpdateCron: (UiCronSetting) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isEnabled = setting.isEnabled
    val displayName = setting.cronExpression.displayName

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = isEnabled && it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {},
            readOnly = true,
            enabled = isEnabled,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .semantics {
                    contentDescription = "Fréquence sélectionnée: $displayName"
                },
            label = { Text("Fréquence") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = colorScheme.outline.copy(alpha = 0.38f),
                disabledLabelColor = colorScheme.onSurface.copy(alpha = 0.38f),
                disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )

        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            CronPreset.entries.forEach { preset ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = preset.displayName,
                            style = typography.bodyMedium
                        )
                    },
                    onClick = {
                        isExpanded = false
                        onUpdateCron(setting.copy(
                            title = preset.displayName,
                            cronExpression = preset.toCronExpression()
                        ))
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Sélectionner ${preset.displayName}"
                    }
                )
            }
        }
    }
}