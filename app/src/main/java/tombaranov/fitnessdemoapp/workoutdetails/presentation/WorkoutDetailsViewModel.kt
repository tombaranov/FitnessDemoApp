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
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoInteractor

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

            try {
                val video = withContext(ioDispatcher) {
                    workoutVideoInteractor.loadWorkoutVideoBy(currentWorkoutId)
                }
                if (video != null) {
                    _videoState.value = VideoUiState.Loaded(videoUrl = video.link)
                } else {
                    _videoState.value = VideoUiState.Error
                }
            } catch (e: Exception) {
                _videoState.value = VideoUiState.Error
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
    data class Loaded(val videoUrl: String) : VideoUiState
    object Error : VideoUiState
}
