package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WorkoutsViewModel : ViewModel() {


    private val _uiState: MutableStateFlow<WorkoutsUiState> =
        MutableStateFlow(WorkoutsUiState.Loading)
    val uiState: StateFlow<WorkoutsUiState>
        get() = _uiState.asStateFlow()
}

sealed interface WorkoutsUiState {

    object Loading : WorkoutsUiState

    data class Loaded(
        val workouts: List<WorkoutUiModel>
    ) : WorkoutsUiState

    object LoadingFailed : WorkoutsUiState
}
