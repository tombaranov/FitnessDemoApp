package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.toUiModel

class WorkoutsViewModel(
    private val workoutsInteractor: WorkoutsInteractor,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WorkoutsUiState> =
        MutableStateFlow(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState>
        get() = _uiState.asStateFlow()

    fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = WorkoutsUiState.Loading

            val loadedWorkouts = withContext(ioDispatcher) {
                workoutsInteractor.loadAll()
            }

            if (loadedWorkouts.isNotEmpty()) {
                val uiWorkouts = loadedWorkouts.map { it.toUiModel() }
                _uiState.value = WorkoutsUiState.Loaded(
                    workouts = uiWorkouts
                )
            } else {
                _uiState.value = WorkoutsUiState.LoadingFailed
            }
        }
    }
}

sealed interface WorkoutsUiState {

    object Loading : WorkoutsUiState

    data class Loaded(
        val workouts: List<WorkoutUiModel>
    ) : WorkoutsUiState

    object LoadingFailed : WorkoutsUiState
}
