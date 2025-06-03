package ygmd.kmpquiz.viewModel.save

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ygmd.kmpquiz.domain.pojo.InternalQanda
import ygmd.kmpquiz.domain.pojo.contentKey
import ygmd.kmpquiz.domain.pojo.correctAnswer
import ygmd.kmpquiz.domain.repository.QandaRepository

class SavedQandasViewModel(
    private val qandaRepository: QandaRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _errorEvents = MutableSharedFlow<String>()
    val errorEvents: SharedFlow<String> = _errorEvents.asSharedFlow()

    // État UI réactif basé sur le Flow du repository
    val savedState: StateFlow<SavedQandasUiState>
        get() = qandaRepository.getAll()
            .combine(_favorites) { qandas, favorites ->
                println("🔄 savedState recalculé - ${qandas.size} qandas") // LOG
                try {
                    if (qandas.isEmpty()) {
                        SavedQandasUiState.Success(
                            qandas = emptyList(),
                            categories = emptyList()
                        )
                    } else {
                        val categories = qandas.map { it.category }.distinct().sorted()
                        SavedQandasUiState.Success(
                            qandas = qandas,
                            categories = categories
                        )
                    }
                } catch (e: Exception) {
                    SavedQandasUiState.Error(
                        message = "Erreur lors du chargement: ${e.message}"
                    )
                }
            }
            .catch { exception ->
                emit(
                    SavedQandasUiState.Error(
                        message = "Impossible de charger vos quiz: ${exception.message}"
                    )
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SavedQandasUiState.Loading
            )

    init {
        loadFavorites()
    }

    fun saveQanda(internalQanda: InternalQanda) {
        viewModelScope.launch {
            try {
                when (val result = qandaRepository.save(internalQanda)) {
                    is Either.Left -> {
//                        _savedState.value = _savedState.value.copy(
//                            error = ViewModelError.SaveError(result.value.message)
//                        )
                    }

                    is Either.Right -> {
//                        val currentSaved = _savedState.value.savedQandas
//                        _savedState.value = _savedState.value.copy(
//                            savedQandas = currentSaved + internalQanda
//                        )
                    }
                }
            } catch (e: Exception) {
//                _savedState.value = _savedState.value.copy(
//                    error = ViewModelError.UnknownError("Unkown error", e)
//                )
            }
        }
    }

    fun saveAll(qandas: List<InternalQanda>) {
        viewModelScope.launch {
            when (val result = qandaRepository.saveAll(qandas)) {
                is Either.Right -> {
//                    _savedState.value = _savedState.value.copy(
//                        savedQandas = _savedState.value.savedQandas + qandas
//                    )
                }

                is Either.Left -> {
//                    _savedState.value = _savedState.value.copy(
//                        error = ViewModelError.SaveError(result.value.message)
//                    )
                }
            }
        }
    }

    fun deleteQanda(qanda: InternalQanda) {
        viewModelScope.launch {
            try {
                when (qandaRepository.deleteById(qanda.id!!)) {
                    is arrow.core.Either.Right -> {
                        // Le Flow se met à jour automatiquement
                        // Supprimer des favoris si nécessaire
                        val currentFavorites = _favorites.value
                        _favorites.value = currentFavorites - qanda.contentKey()
                    }

                    is arrow.core.Either.Left -> {
                        _errorEvents.emit("Impossible de supprimer ce quiz")
                    }
                }
            } catch (e: Exception) {
                _errorEvents.emit("Erreur lors de la suppression: ${e.message}")
            }
        }
    }

    fun toggleFavorite(qanda: InternalQanda) {
        viewModelScope.launch {
            val contentKey = qanda.contentKey()
            val currentFavorites = _favorites.value

            _favorites.value = if (currentFavorites.contains(contentKey)) {
                currentFavorites - contentKey
            } else {
                currentFavorites + contentKey
            }

            // Sauvegarder en local storage
            saveFavoritesToStorage(_favorites.value)
        }
    }

    fun searchQandas(query: String): StateFlow<List<InternalQanda>> {
        return savedState
            .map { state ->
                when (state) {
                    is SavedQandasUiState.Success -> {
                        if (query.isBlank()) {
                            state.qandas
                        } else {
                            state.qandas.filter { qanda ->
                                qanda.question.contains(query, ignoreCase = true) ||
                                        qanda.category.contains(query, ignoreCase = true) ||
                                        qanda.correctAnswer().contains(query, ignoreCase = true)
                            }
                        }
                    }

                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    }

    fun filterByCategory(category: String?): StateFlow<List<InternalQanda>> {
        return savedState
            .map { state ->
                when (state) {
                    is SavedQandasUiState.Success -> {
                        if (category != null) {
                            state.qandas.filter { it.category == category }
                        } else {
                            state.qandas
                        }
                    }

                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    }

    // Statistiques réactives
    val quizStats: StateFlow<QuizStats> = savedState
        .combine(_favorites) { state, favorites ->
            when (state) {
                is SavedQandasUiState.Success -> {
                    val qandas = state.qandas
                    QuizStats(
                        totalQuiz = qandas.size,
                        categoriesCount = state.categories.size,
                        favoritesCount = favorites.size,
                        difficultyBreakdown = mapOf(
                            "easy" to qandas.count { it.difficulty?.lowercase() == "easy" },
                            "medium" to qandas.count { it.difficulty?.lowercase() == "medium" },
                            "hard" to qandas.count { it.difficulty?.lowercase() == "hard" }
                        ),
                        recentlyAdded = qandas.take(5) // Les 5 derniers ajoutés
                    )
                }

                else -> QuizStats()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = QuizStats()
        )

    fun getRandomQuiz(count: Int = 10): List<InternalQanda> {
        val currentState = savedState.value
        return if (currentState is SavedQandasUiState.Success) {
            currentState.qandas.shuffled().take(count)
        } else {
            emptyList()
        }
    }

    // Favoris réactifs
    val favoriteQandas: StateFlow<List<InternalQanda>> = savedState
        .combine(_favorites) { state, favoriteKeys ->
            when (state) {
                is SavedQandasUiState.Success -> {
                    state.qandas.filter { qanda ->
                        favoriteKeys.contains(qanda.contentKey())
                    }
                }

                else -> emptyList()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    fun getQandasByDifficulty(difficulty: String): StateFlow<List<InternalQanda>> {
        return savedState
            .map { state ->
                when (state) {
                    is SavedQandasUiState.Success -> {
                        state.qandas.filter {
                            it.difficulty?.lowercase() == difficulty.lowercase()
                        }
                    }

                    else -> emptyList()
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = emptyList()
            )
    }

    fun bulkDeleteQandas(qandas: List<InternalQanda>) {
        viewModelScope.launch {
            try {
                qandas.forEach { qanda ->
                    qandaRepository.deleteById(qanda.id!!)
                }
                // Le Flow se met à jour automatiquement
                // Nettoyer les favoris
                val keysToRemove = qandas.map { it.contentKey() }.toSet()
                _favorites.value -= keysToRemove

            } catch (e: Exception) {
                _errorEvents.emit("Erreur lors de la suppression multiple: ${e.message}")
            }
        }
    }

    fun exportQandas(): String {
        val currentState = savedState.value
        return if (currentState is SavedQandasUiState.Success) {
            // Format JSON simple pour l'export
            currentState.qandas.joinToString("\n") { qanda ->
                "Q: ${qanda.question}\nR: ${qanda.correctAnswer()}\nCatégorie: ${qanda.category}\nDifficulté: ${qanda.difficulty}\n---"
            }
        } else {
            ""
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                // Charger les favoris depuis le stockage local
                // Pour l'instant, on simule avec un set vide
                _favorites.value = emptySet()
                // TODO: Implémenter le chargement depuis SharedPreferences ou base de données
                // val savedFavorites = sharedPreferences.getStringSet("favorites", emptySet()) ?: emptySet()
                // _favorites.value = savedFavorites
            } catch (e: Exception) {
                _favorites.value = emptySet()
            }
        }
    }

    private suspend fun saveFavoritesToStorage(favorites: Set<String>) {
        try {
            // TODO: Sauvegarder les favoris dans SharedPreferences ou base de données
            // sharedPreferences.edit().putStringSet("favorites", favorites).apply()
        } catch (e: Exception) {
            // Log l'erreur
        }
    }

    fun isFavorite(qanda: InternalQanda): Boolean {
        return _favorites.value.contains(qanda.contentKey())
    }

    fun clearAllData() {
        viewModelScope.launch {
            try {
                _favorites.value = emptySet()
                saveFavoritesToStorage(emptySet())
            } catch (e: Exception) {
                _errorEvents.emit("Erreur lors de la suppression: ${e.message}")
            }
        }
    }

    // Fonction utilitaire pour forcer un refresh (si nécessaire)
    fun refreshData() {
        // Avec Flow, généralement pas nécessaire
        // Mais peut être utile pour certains cas edge
        viewModelScope.launch {
            try {
                // Force la re-émission du Flow si nécessaire
                // Généralement, le repository gère ça automatiquement
            } catch (e: Exception) {
                _errorEvents.emit("Erreur lors du rafraîchissement: ${e.message}")
            }
        }
    }
}

// États UI inchangés
sealed class SavedQandasUiState {
    data object Loading : SavedQandasUiState()

    class Success(
        val qandas: List<InternalQanda>,
        val categories: List<String>
    ) : SavedQandasUiState() {
        fun containsContentKey(contentKey: String) =
            qandas.map { it.contentKey() }.any { it == contentKey }
    }

    data class Error(
        val message: String
    ) : SavedQandasUiState()
}

// Modèle pour les statistiques inchangé
data class QuizStats(
    val totalQuiz: Int = 0,
    val categoriesCount: Int = 0,
    val favoritesCount: Int = 0,
    val difficultyBreakdown: Map<String, Int> = emptyMap(),
    val recentlyAdded: List<InternalQanda> = emptyList()
)