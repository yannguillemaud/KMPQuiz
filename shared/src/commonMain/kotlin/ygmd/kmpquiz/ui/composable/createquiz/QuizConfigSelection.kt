
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizMode

@Composable
fun QuizConfigSection(
    quizMode: DisplayableQuizMode,
    onModeSelected: (DisplayableQuizMode) -> Unit,
    onGlobalLimitChanged: (Int) -> Unit,
    onCategoryLimitChanged: (String, Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader(title = "Question Limits")

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            val modes = listOf("Full", "Limited", "Specific")
            modes.forEachIndexed { index, label ->
                val isSelected = when(index) {
                    0 -> quizMode is DisplayableQuizMode.Full
                    1 -> quizMode is DisplayableQuizMode.Limited
                    else -> quizMode is DisplayableQuizMode.ByCategory
                }
                SegmentedButton(
                    selected = isSelected,
                    onClick = {
                        val newMode = when(index) {
                            0 -> DisplayableQuizMode.Full
                            1 -> DisplayableQuizMode.Limited(0, 50)
                            else -> DisplayableQuizMode.ByCategory()
                        }
                        onModeSelected(newMode)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                ) { Text(label) }
            }
        }

        // Display sliders directly based on mode
        when (quizMode) {
            is DisplayableQuizMode.Limited -> {
                LimitItem(label = "Overall Limit", value = quizMode.limit, max = quizMode.max) { onGlobalLimitChanged(it) }
            }
            is DisplayableQuizMode.ByCategory -> {
                quizMode.limits.forEach { (cat, current) ->
                    LimitItem(label = cat.name, value = current, max = cat.questionsCount) { onCategoryLimitChanged(cat.id, it) }
                }
            }
            else -> {
                Text(
                    "All available questions will be used.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

/**
 * Enhanced slider item with a subtle container to prevent "fusion" with other sliders.
 */
@Composable
private fun LimitItem(label: String, value: Int, max: Int, onValueChange: (Int) -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("$value / $max", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..max.toFloat().coerceAtLeast(1f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}