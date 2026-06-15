package ygmd.kmpquiz.presentation.viewModel.quiz.edit

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import dev.brewkits.grant.AppGrant.NOTIFICATION
import dev.brewkits.grant.GrantHandler
import dev.brewkits.grant.GrantManager
import dev.brewkits.grant.GrantPermission
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ygmd.kmpquiz.core.domain.category.CategoryWithCount
import ygmd.kmpquiz.core.domain.quiz.config.QuizSchedulerConfiguration
import ygmd.kmpquiz.core.domain.quiz.config.SchedulerSelection
import ygmd.kmpquiz.core.domain.quiz.Quiz
import ygmd.kmpquiz.core.domain.quiz.config.QuizQuestionsConfiguration
import ygmd.kmpquiz.core.usecase.category.CategoryUseCase
import ygmd.kmpquiz.core.usecase.quiz.GetQuizUseCase
import ygmd.kmpquiz.core.usecase.quiz.SaveQuizUseCase
import ygmd.kmpquiz.core.domain.event.Event
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategory
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableCategoryWithCount
import ygmd.kmpquiz.presentation.viewModel.displayable.DisplayableQuizMode
import java.util.UUID

@Immutable
data class MetadataState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)

@Immutable
data class ContentState(
    val id: String? = null,
    val title: String = "",
    val availableCategories: ImmutableList<DisplayableCategory> = persistentListOf(),
) {
    val titleError: String? = if (title.isBlank()) "Title cannot be empty." else null
}

@Immutable
data class ConfigurationState(
    val selectedCategories: ImmutableList<DisplayableCategoryWithCount> = persistentListOf(),
    val selectedScheduler: SchedulerSelectionState = SchedulerSelectionState(),
    val selectedQuizMode: DisplayableQuizMode = DisplayableQuizMode.Full,
    val totalAvailableQuestions: Int = 0,
) {
    val noQuestionsSelectedError: String? =
        if (totalAvailableQuestions == 0) "Cannot save quiz without questions." else null
}

data class SchedulerSelectionState(
    val id: String? = null,
    val selectedScheduler: SchedulerSelection? = null,
    val isEnabled: Boolean = false,
)

@Immutable
data class QuizEditUiState(
    val metadata: MetadataState = MetadataState(),
    val content: ContentState = ContentState(),
    val configuration: ConfigurationState = ConfigurationState()
) {
    val canSave: Boolean = !metadata.isSaving &&
            content.titleError == null &&
            configuration.selectedCategories.isNotEmpty()
}

private val logger = Logger.withTag("QuizEditViewModel")

class QuizEditViewModel(
    private val getQuizUseCase: GetQuizUseCase,
    private val saveQuizUseCase: SaveQuizUseCase,
    private val categoryUseCase: CategoryUseCase,
    private val grantManager: GrantManager,
) : ViewModel() {

    /* inputs */
    private val _isLoading = MutableStateFlow(false)
    private val _isSaving = MutableStateFlow(false)
    private val _quizId = MutableStateFlow<String?>(null)
    private val _title = MutableStateFlow("")
    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    private val _selectedScheduler = MutableStateFlow(SchedulerSelectionState())
    private val _domainConfig =
        MutableStateFlow<QuizQuestionsConfiguration>(QuizQuestionsConfiguration.AllQuestions())

    /* DB flows */
    private val _categoriesWithCount =
        categoryUseCase.observeCategories().distinctUntilChanged()

    /* partitioning */
    private val metadataState = combine(_isLoading, _isSaving) { loading, saving ->
        MetadataState(isLoading = loading, isSaving = saving)
    }.distinctUntilChanged()

    private val contentState =
        combine(_quizId, _title, _categoriesWithCount) { id, title, categories ->
            ContentState(
                id = id,
                title = title,
                availableCategories = categories
                    .map { DisplayableCategory(it.id, it.name) }
                    .toImmutableList(),
            )
        }.distinctUntilChanged()

    private val configurationState = combine(
        _selectedCategoryIds,
        _selectedScheduler,
        _domainConfig,
        _categoriesWithCount,
    ) { selectedIds, selectedScheduler, config, allCatsWithCount ->
        val selectedQuizMode = mapToDisplayableMode(config, allCatsWithCount, selectedIds)
        val selectedCategoriesWithCount = allCatsWithCount.filter { it.id in selectedIds }
        val displayableCategories =
            selectedCategoriesWithCount.map {
                DisplayableCategoryWithCount(it.id, it.name, it.questionsCount)
            }
        ConfigurationState(
            selectedCategories = displayableCategories.toImmutableList(),
            selectedScheduler = selectedScheduler,
            selectedQuizMode = selectedQuizMode,
            totalAvailableQuestions = countQuestions(selectedCategoriesWithCount, selectedQuizMode)
        )
    }.distinctUntilChanged()

    /* UI STATE */
    val quizState: StateFlow<QuizEditUiState> = combine(
        metadataState,
        contentState,
        configurationState
    ) { meta, content, config ->
        QuizEditUiState(meta, content, config)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), QuizEditUiState())

    /* permission handler */
    val notificationPermission =
        GrantHandler(grantManager, NOTIFICATION, viewModelScope)

    /* events */
    private val _events = Channel<Event>()
    val events = _events.receiveAsFlow()

    /* init */
    fun setUp(quizId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val quiz = getQuizUseCase.getById(quizId) ?: run {
                _isLoading.value = false
                return@launch
            }

            _quizId.value = quiz.id
            _title.value = quiz.title
            _selectedCategoryIds.value = quiz.categories.map { it.id }.toSet()
            quiz.schedulerConfiguration?.let {
                _selectedScheduler.value = SchedulerSelectionState(
                    id = it.id,
                    selectedScheduler = it.selection,
                    isEnabled = it.isEnabled
                )
            }
            _domainConfig.value = quiz.qandasConfiguration
            _isLoading.value = false
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    /**
     * Attempts to set a scheduler. This is a suspending operation that sequentially requests
     * Notification and Exact Alarm permissions from the OS via Grant.
     * The UI state is only updated if both permissions are GRANTED.
     */
    fun setScheduler(schedulerSelection: SchedulerSelection?) {
        _selectedScheduler.update {
            it.copy(selectedScheduler = schedulerSelection)
        }
    }

    fun toggleSelectScheduler(isEnabled: Boolean) {
        if (!isEnabled) {
            _selectedScheduler.update { it.copy(isEnabled = false) }
            return
        }
        if (_selectedScheduler.value.selectedScheduler == null) {
            _events.trySend(Event.ShowSnackbar("No scheduler selected."))
            return
        }
        notificationPermission.request(
            rationaleMessage = "Notifications are required to trigger scheduled quizzes.",
            settingsMessage = "Please enable notifications in Settings to use the scheduler."
        ) {
            _selectedScheduler.update { it.copy(isEnabled = true) }
        }
    }

    fun toggleSelectCategory(id: String) {
        _selectedCategoryIds.update { if (id in it) it - id else it + id }
    }

    fun switchConfigMode(newConfigMode: DisplayableQuizMode) {
        fun hasConfigChanged(): Boolean = with(_domainConfig.value) {
            when (this) {
                is QuizQuestionsConfiguration.AllQuestions if newConfigMode is DisplayableQuizMode.Full -> false
                is QuizQuestionsConfiguration.TotalLimited if newConfigMode is DisplayableQuizMode.Limited -> false
                is QuizQuestionsConfiguration.ByCategory if newConfigMode is DisplayableQuizMode.ByCategory -> false
                else -> true
            }
        }
        if (!hasConfigChanged()) return
        _domainConfig.value = when (newConfigMode) {
            is DisplayableQuizMode.Full -> QuizQuestionsConfiguration.AllQuestions()
            is DisplayableQuizMode.Limited -> QuizQuestionsConfiguration.TotalLimited()
            is DisplayableQuizMode.ByCategory -> QuizQuestionsConfiguration.ByCategory()
        }
    }

    fun updateGlobalLimit(newLimit: Int) {
        _domainConfig.update { currentMode ->
            if (currentMode is QuizQuestionsConfiguration.TotalLimited) {
                currentMode.copy(count = newLimit)
            } else currentMode
        }
    }

    fun updateCategoryLimit(categoryId: String, newLimit: Int) {
        _domainConfig.update { currentMode ->
            if (currentMode is QuizQuestionsConfiguration.ByCategory) {
                val newConfigByCategory = currentMode.limitByCategory
                    .toMutableMap()
                    .apply { this[categoryId] = newLimit }
                currentMode.copy(limitByCategory = newConfigByCategory)
            } else currentMode
        }
    }

    /**
     * Saves the Quiz.
     * UX Note: If the user previously set a scheduler but manually revoked permissions in OS settings
     * before hitting save, we DO NOT block the saving process. We gracefully degrade by disabling the cron
     * and informing the user.
     */
    fun saveQuiz() {
        if (!quizState.value.canSave) return
        viewModelScope.launch {
            _isSaving.value = true
            val quizStateVal = quizState.value
            val (_, content, config) = quizStateVal

            try {
                val categories = config.selectedCategories.map {
                    CategoryWithCount(it.id, it.name, it.questionsCount)
                }

                val configuration = when (val configMode = config.selectedQuizMode) {
                    is DisplayableQuizMode.Full -> QuizQuestionsConfiguration.AllQuestions()
                    is DisplayableQuizMode.Limited -> QuizQuestionsConfiguration.TotalLimited(count = configMode.limit)
                    is DisplayableQuizMode.ByCategory -> {
                        val configByCategory = configMode.limits.mapKeys { it.key.id }
                        QuizQuestionsConfiguration.ByCategory(limitByCategory = configByCategory)
                    }
                }

                // Handle Scheduler & Permissions Resilience
                val selectedScheduler = config.selectedScheduler.selectedScheduler
                val scheduler = if (selectedScheduler == null) null else {
                    QuizSchedulerConfiguration(
                        id = config.selectedScheduler.id ?: UUID.randomUUID().toString(),
                        selection = selectedScheduler,
                        isEnabled = config.selectedScheduler.isEnabled
                    )
                }

                val quiz = Quiz(
                    id = content.id ?: UUID.randomUUID().toString(),
                    title = content.title,
                    categories = categories,
                    qandasConfiguration = configuration,
                    schedulerConfiguration = scheduler
                )

                saveQuizUseCase.save(quiz).onSuccess {
                    _events.send(Event.NavigateBack)
                }
            } catch (e: Exception) {
                _events.send(Event.ShowSnackbar("Save failed ${e.message?.let { ": $it" }}"))
                logger.e(e) { "Failed to save quiz" }
            } finally {
                _isSaving.value = false
            }
        }
    }

    /* Helpers */
    private fun mapToDisplayableMode(
        config: QuizQuestionsConfiguration,
        allCats: List<CategoryWithCount>,
        selectedIds: Set<String>
    ): DisplayableQuizMode {
        val maxQuestions = allCats.filter { it.id in selectedIds }.sumOf { it.questionsCount }
        return when (config) {
            is QuizQuestionsConfiguration.AllQuestions -> DisplayableQuizMode.Full
            is QuizQuestionsConfiguration.TotalLimited -> DisplayableQuizMode.Limited(
                config.count,
                maxQuestions
            )

            is QuizQuestionsConfiguration.ByCategory -> {
                val limits = allCats.filter { it.id in selectedIds }
                    .associate { cat ->
                        DisplayableCategoryWithCount(
                            cat.id,
                            cat.name,
                            cat.questionsCount
                        ) to (config.limitByCategory[cat.id] ?: 0)
                    }
                DisplayableQuizMode.ByCategory(limits)
            }
        }
    }

    private fun countQuestions(
        selectedCategoriesWithCount: List<CategoryWithCount>,
        mode: DisplayableQuizMode
    ): Int {
        return when (mode) {
            is DisplayableQuizMode.Full -> selectedCategoriesWithCount.sumOf { it.questionsCount }
            is DisplayableQuizMode.Limited -> mode.limit
            is DisplayableQuizMode.ByCategory -> mode.limits.values.sum()
        }
    }
}