
import androidx.compose.foundation.lazy.LazyListScope
import ygmd.kmpquiz.domain.model.cron.SchedulerSelection
import ygmd.kmpquiz.domain.viewModel.displayable.DisplayableQuizMode
import ygmd.kmpquiz.domain.viewModel.quiz.edit.QuizEditUiState

/**
 * Used as an extension to not sacrifice scroll behaviour
 */
fun LazyListScope.quizSettingsFormItems(
    quiz: QuizEditUiState,
    onTitleChange: (String) -> Unit,
    onCategoryToggled: (String) -> Unit,
    onSchedulerConfigurationChanged: (SchedulerSelection) -> Unit,
    onRemoveScheduler: () -> Unit,
    onSwitchConfig: (DisplayableQuizMode) -> Unit,
    onGlobalLimitChanged: (Int) -> Unit,
    onCategoryLimitChanged: (String, Int) -> Unit,
) {

}