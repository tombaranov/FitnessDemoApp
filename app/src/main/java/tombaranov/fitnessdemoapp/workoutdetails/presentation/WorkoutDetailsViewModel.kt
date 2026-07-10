package tombaranov.fitnessdemoapp.workoutdetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoInteractor
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoResult

class WorkoutDetailsViewModel(
    private val workoutVideoInteractor: WorkoutVideoInteractor,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private var currentWorkoutId: Int = INVALID_WORKOUT_ID

    private var loadWorkoutVideoJob: Job? = null

    private val _videoState: MutableStateFlow<VideoUiState> =
        MutableStateFlow(VideoUiState.Loading)

    val videoState: StateFlow<VideoUiState>
        get() = _videoState.asStateFlow()

    fun loadVideo(workoutId: Int) {
        currentWorkoutId = workoutId
        loadVideo()
    }

    fun retryLoadVideo() {
        loadVideo()
    }

    private fun loadVideo() {
        cancelLoadWorkoutVideoJob()

        loadWorkoutVideoJob = viewModelScope.launch {
            _videoState.value = VideoUiState.Loading

            when (val result = withContext(ioDispatcher) {
                workoutVideoInteractor.loadWorkoutVideoBy(currentWorkoutId)
            }) {
                is WorkoutVideoResult.Success -> {
                    _videoState.value = VideoUiState.Loaded(video = result.video)
                }

                WorkoutVideoResult.ClientError -> {
                    _videoState.value = VideoUiState.ClientError
                }

                WorkoutVideoResult.ServerError -> {
                    _videoState.value = VideoUiState.ServerError
                }
            }
        }
    }

    private fun cancelLoadWorkoutVideoJob() {
        if (loadWorkoutVideoJob != null) {
            loadWorkoutVideoJob?.cancel()
            loadWorkoutVideoJob = null
        }
    }

    companion object {
        private const val INVALID_WORKOUT_ID = -1
    }
}

sealed interface VideoUiState {
    object Loading : VideoUiState
    data class Loaded(val video: WorkoutVideo) : VideoUiState
    object ClientError : VideoUiState
    object ServerError : VideoUiState
}
