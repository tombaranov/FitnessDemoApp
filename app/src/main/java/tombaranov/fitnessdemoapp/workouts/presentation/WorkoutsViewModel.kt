package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tombaranov.fitnessdemoapp.workouts.domain.Type
import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsResult

class WorkoutsViewModel(
    private val workoutsInteractor: WorkoutsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
    private val searchHelper: SearchHelper,
) : ViewModel() {

    private var loadWorkoutsJob: Job? = null
    private var savedWorkouts: List<Workout> = emptyList()

    private val _uiState: MutableStateFlow<WorkoutsUiState> =
        MutableStateFlow(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState>
        get() = _uiState.asStateFlow()

    fun loadWorkouts() {
        cancelLoadWorkoutsJob()

        loadWorkoutsJob = viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading

            val workoutsResult = withContext(ioDispatcher) {
                workoutsInteractor.loadAll()
            }

            when (workoutsResult) {
                is WorkoutsResult.Success -> {
                    if (workoutsResult.workouts.isNotEmpty()) {
                        savedWorkouts = workoutsResult.workouts

                        _uiState.value = WorkoutsUiState.Loaded(
                            workouts = workoutsResult.workouts,
                        )
                    } else {
                        _uiState.value = WorkoutsUiState.ClientError
                    }
                }

                WorkoutsResult.ClientError -> {
                    _uiState.value = WorkoutsUiState.ClientError
                }

                WorkoutsResult.ServerError -> {
                    _uiState.value = WorkoutsUiState.ServerError
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        applyFilters(
            query = query,
            type = getSelectedFilterType()
        )
    }

    fun onTypeFilterSelected(type: Type?) {
        applyFilters(
            query = getEnteredSearchQuery(),
            type = type
        )
    }

    fun resetFilters() {
        applyFilters(
            query = getEnteredSearchQuery(),
            type = null
        )
    }

    private fun getEnteredSearchQuery(): String {
        return (_uiState.value as? WorkoutsUiState.Loaded)?.searchQuery.orEmpty()
    }

    private fun getSelectedFilterType(): Type? {
        return (_uiState.value as? WorkoutsUiState.Loaded)?.selectedType
    }

    private fun applyFilters(query: String = "", type: Type?) {
        val filtered = searchHelper.apply(savedWorkouts, type, query)

        _uiState.value = WorkoutsUiState.Loaded(
            workouts = filtered,
            searchQuery = query,
            selectedType = type,
        )
    }

    private fun cancelLoadWorkoutsJob() {
        if (loadWorkoutsJob != null) {
            loadWorkoutsJob?.cancel()
            loadWorkoutsJob = null
        }
    }
}

sealed interface WorkoutsUiState {
    object Loading : WorkoutsUiState
    data class Loaded(
        val workouts: List<Workout> = emptyList(),
        val searchQuery: String = "",
        val selectedType: Type? = null,
    ) : WorkoutsUiState

    object ClientError : WorkoutsUiState
    object ServerError : WorkoutsUiState
}
