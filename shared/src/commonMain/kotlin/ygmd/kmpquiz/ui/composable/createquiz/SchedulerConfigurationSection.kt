package ygmd.kmpquiz.ui.composable.createquiz

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.model.scheduler.SchedulerSelection

@Composable
fun SchedulerConfigurationSection(
    currentSelection: SchedulerSelection?,
    isEnabled: Boolean,
    onSelectionChange: (SchedulerSelection?) -> Unit,
    onToggleEnabled: (Boolean) -> Unit = {},
    defaultHour: Int = 12,
    defaultMinute: Int = 0
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (isEnabled) 1f else 0.6f,
        label = "alpha"
    )

    val schedulerSelectionLabel = currentSelection?.explicitName ?: "Select scheduling time"

    Column(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker = true },
            headlineContent = {
                Text(
                    text = schedulerSelectionLabel,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alpha(contentAlpha)
                )
            },
            supportingContent = {
                AnimatedVisibility(visible = isEnabled) {
                    Text(
                        text = "Quiz will be automatically scheduled",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.alpha(contentAlpha)
                )
            },
            trailingContent = {
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggleEnabled,
                    thumbContent = if (isEnabled) {
                        {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.padding(2.dp)
                            )
                        }
                    } else null
                )
            },
            colors = ListItemDefaults.colors(containerColor = Transparent)
        )
    }

    if (showTimePicker) {
        val initialHour =
            (currentSelection as? SchedulerSelection.SpecificTime)?.hour ?: defaultHour
        val initialMinute =
            (currentSelection as? SchedulerSelection.SpecificTime)?.minute ?: defaultMinute

        TimeSelectorDialog(
            initialHour = initialHour,
            initialMinute = initialMinute,
            onConfirm = { hour, minute ->
                onSelectionChange(SchedulerSelection.SpecificTime(hour, minute))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelectorDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.hour, timePickerState.minute)
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}