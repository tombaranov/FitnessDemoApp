package tombaranov.fitnessdemoapp.workoutdetails.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import tombaranov.fitnessdemoapp.player.PlayerEvent
import tombaranov.fitnessdemoapp.player.VideoPlayerManager
import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.databinding.FragmentWorkoutDetailsBinding
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutsFragment

class WorkoutDetailsFragment : Fragment(R.layout.fragment_workout_details) {

    private lateinit var binding: FragmentWorkoutDetailsBinding
    private val viewModel: WorkoutDetailsViewModel by viewModel()
    private val playerManager: VideoPlayerManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentWorkoutDetailsBinding.bind(view)

        observeVideoState()
        observePlayerEvents()

        setupWorkoutInfo()
        loadVideo()
    }

    private fun formatDurationLabel(duration: String): String {
        val totalMinutes = duration.toIntOrNull()
        val formatted = totalMinutes?.let { DurationFormatter.format(it, resources) } ?: duration
        return getString(R.string.workout_details_duration_label, formatted)
    }

    private fun observeVideoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoState.collect { state ->
                    when (state) {
                        VideoUiState.Loading -> showLoading()
                        is VideoUiState.Loaded -> showVideo(state.videoUrl)
                        VideoUiState.ClientError -> showClientError()
                        VideoUiState.ServerError -> showServerError()
                    }
                }
            }
        }
    }

    private fun observePlayerEvents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                playerManager.events.collect { event ->
                    when (event) {
                        PlayerEvent.Error -> showServerError()
                    }
                }
            }
        }
    }

    private fun setupWorkoutInfo() {
        arguments?.let { args ->
            binding.detailsTitle.text =
                args.getString(WorkoutsFragment.ARG_WORKOUT_TITLE, DEFAULT_WORKOUT_TITLE)
            binding.detailsType.text =
                getString(
                    R.string.workout_details_type_label,
                    args.getString(WorkoutsFragment.ARG_WORKOUT_TYPE, DEFAULT_WORKOUT_TYPE)
                )
            binding.detailsDuration.text = formatDurationLabel(
                args.getString(WorkoutsFragment.ARG_WORKOUT_DURATION, DEFAULT_WORKOUT_DURATION)
            )
            binding.detailsDescription.text =
                args.getString(
                    WorkoutsFragment.ARG_WORKOUT_DESCRIPTION,
                    DEFAULT_WORKOUT_DESCRIPTION
                )
                    .takeUnless { it.isNullOrBlank() }
                    ?: getString(R.string.workout_details_no_description)
        }

        binding.retryVideoButton.setOnClickListener { viewModel.retryLoadVideo() }
    }

    private fun loadVideo() {
        val workoutId = arguments?.getInt(WorkoutsFragment.ARG_WORKOUT_ID, INVALID_WORKOUT_ID)
            ?: INVALID_WORKOUT_ID

        if (workoutId <= 0) {
            showClientError()
            return
        }

        viewModel.loadVideo(workoutId)
    }

    private fun showLoading() {
        binding.videoPlayer.isVisible = false
        binding.videoErrorContainer.isVisible = false
        binding.videoLoadingProgress.isVisible = true
    }

    private fun showVideo(videoUrl: String) {
        binding.videoLoadingProgress.isVisible = false
        binding.videoErrorContainer.isVisible = false
        binding.videoPlayer.isVisible = true
        playerManager.initialize(binding.videoPlayer)
        playerManager.prepare(videoUrl)
    }

    private fun showClientError() {
        binding.videoLoadingProgress.isVisible = false
        binding.videoPlayer.isVisible = false
        binding.videoErrorContainer.isVisible = true
        binding.videoErrorText.setText(R.string.video_load_client_error)
        binding.retryVideoButton.isVisible = false
    }

    private fun showServerError() {
        binding.videoLoadingProgress.isVisible = false
        binding.videoPlayer.isVisible = false
        binding.videoErrorContainer.isVisible = true
        binding.videoErrorText.setText(R.string.workouts_error)
        binding.retryVideoButton.isVisible = true
    }

    override fun onStop() {
        super.onStop()
        playerManager.saveAndRelease()
        binding.videoPlayer.player = null
    }

    companion object {
        const val DEFAULT_WORKOUT_TITLE = ""
        const val DEFAULT_WORKOUT_TYPE = ""
        const val DEFAULT_WORKOUT_DURATION = ""
        const val DEFAULT_WORKOUT_DESCRIPTION = ""

        const val INVALID_WORKOUT_ID = -1

        fun newInstance() = WorkoutDetailsFragment()
    }
}
