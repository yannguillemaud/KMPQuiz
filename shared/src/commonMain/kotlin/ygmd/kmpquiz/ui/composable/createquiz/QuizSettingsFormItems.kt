
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizMode
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditUiState
import ygmd.kmpquiz.ui.composable.createquiz.CronReminderSelection

/**
 * Used as an extension to not sacrifice scroll behaviour
 */
fun LazyListScope.quizSettingsFormItems(
    quiz: QuizEditUiState,
    onTitleChange: (String) -> Unit,
    onCategoryToggled: (String) -> Unit,
    onCronChange: (String?) -> Unit,
    onSwitchConfig: (DisplayableQuizMode) -> Unit,
    onGlobalLimitChanged: (Int) -> Unit,
    onCategoryLimitChanged: (String, Int) -> Unit
) {
    item(key = "title_field") {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            value = quiz.content.title,
            onValueChange = onTitleChange,
            label = { Text("Titre du Quiz", fontWeight = FontWeight.Bold) },
            isError = quiz.content.titleError != null,
            supportingText = { quiz.content.titleError?.let { Text(it) } },
            shape = MaterialTheme.shapes.medium
        )
    }

    item(key = "categories_section") {
        CategorySelectionSection(
            categories = quiz.content.availableCategories,
            selectedCategories = quiz.configuration.selectedCategories,
            onCategoryToggled = onCategoryToggled
        )
    }

    item(key = "cron_section") {
        CronReminderSelection(
            availableCrons = quiz.content.availableCrons,
            initialSelected = quiz.configuration.selectedCron,
            onSelectionChanged = onCronChange
        )
    }

    item(key = "config_section") {
        QuizConfigSection(
            quizMode = quiz.configuration.selectedQuizMode,
            onModeSelected = onSwitchConfig,
            onGlobalLimitChanged = onGlobalLimitChanged,
            onCategoryLimitChanged = onCategoryLimitChanged
        )
    }

    item(key = "questions_count") {
        Text("Quiz will contain ${quiz.configuration.totalAvailableQuestions} questions")
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        letterSpacing = 1.sp
    )
}