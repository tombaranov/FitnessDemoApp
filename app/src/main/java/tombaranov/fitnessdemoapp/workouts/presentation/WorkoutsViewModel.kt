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
import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsResult

class WorkoutsViewModel(
    private val workoutsInteractor: WorkoutsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var loadWorkoutsJob: Job? = null

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
                        _uiState.value = WorkoutsUiState.Loaded(workouts = workoutsResult.workouts)
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

    private fun cancelLoadWorkoutsJob() {
        if (loadWorkoutsJob != null) {
            loadWorkoutsJob?.cancel()
            loadWorkoutsJob = null
        }
    }
}

sealed interface WorkoutsUiState {
    object Loading : WorkoutsUiState
    data class Loaded(val workouts: List<Workout>) : WorkoutsUiState
    object ClientError : WorkoutsUiState
    object ServerError : WorkoutsUiState
}
