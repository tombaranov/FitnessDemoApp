package tombaranov.fitnessdemoapp.workoutdetails.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutsFragment

// TODO: Провести рефакторинг после завершения работы
class WorkoutDetailsFragment : Fragment(R.layout.fragment_workout_details) {

    private lateinit var binding: FragmentWorkoutDetailsBinding
    private val viewModel: WorkoutDetailsViewModel by viewModel()
    private val playerManager: VideoPlayerManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkoutDetailsBinding.bind(view)
        trackOrientationChanges()

        observeVideoState()
        observePlayerEvents()

        setupWorkoutInfo()
        loadVideo()
    }

    private fun trackOrientationChanges() {
        val orientation = resources.configuration.orientation

        when (orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> enterFullscreenMode()
            Configuration.ORIENTATION_PORTRAIT -> exitFullscreenMode()
            else -> Unit
        }
    }

    // TODO: Провести рефакторинг после завершения работы
    private fun enterFullscreenMode() {
        binding.detailsTitle.isVisible = false
        binding.workoutInfo.isVisible = false

        val playerContainerLayoutParams = binding.videoContainer.layoutParams as android.widget.LinearLayout.LayoutParams
        playerContainerLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerContainerLayoutParams.weight = 1f
        playerContainerLayoutParams.setMargins(0, 0, 0, 0)
        binding.videoContainer.layoutParams = playerContainerLayoutParams

        val windowInsetsController = activity?.window?.let {
            WindowInsetsControllerCompat(it, binding.videoContainer)
        }
        windowInsetsController?.hide(WindowInsetsCompat.Type.systemBars())
        windowInsetsController?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    // TODO: Провести рефакторинг после завершения работы
    private fun exitFullscreenMode() {
        binding.detailsTitle.isVisible = true
        binding.workoutInfo.isVisible = true

        val playerContainerLayoutParams = binding.videoContainer.layoutParams as android.widget.LinearLayout.LayoutParams
        playerContainerLayoutParams.height = resources.getDimensionPixelSize(R.dimen.video_player_height)
        playerContainerLayoutParams.weight = 0f
        val margin = resources.getDimensionPixelSize(R.dimen.video_player_margin)
        playerContainerLayoutParams.setMargins(margin, margin / 2, margin, 0)
        binding.videoContainer.layoutParams = playerContainerLayoutParams

        val windowInsetsController = activity?.window?.let {
            WindowInsetsControllerCompat(it, binding.videoContainer)
        }
        windowInsetsController?.show(WindowInsetsCompat.Type.systemBars())
    }

    private fun setupWorkoutInfo() {
        arguments?.let { args ->
            binding.detailsTitle.text =
                args.getString(WorkoutsFragment.ARG_WORKOUT_TITLE, "")
            binding.detailsType.text =
                getString(
                    R.string.workout_details_type_label,
                    args.getString(WorkoutsFragment.ARG_WORKOUT_TYPE, "")
                )
            binding.detailsDescription.text =
                args.getString(WorkoutsFragment.ARG_WORKOUT_DESCRIPTION, "")
                    .takeUnless { it.isNullOrBlank() }
                    ?: getString(R.string.workout_details_no_description)
        }
    }

    private fun observeVideoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoState.collect { state ->
                    when (state) {
                        VideoUiState.Loading -> showLoading()
                        is VideoUiState.Loaded -> showVideo(state.video)
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

    private fun showVideo(video: WorkoutVideo) {
        binding.videoLoadingProgress.isVisible = false
        binding.videoErrorContainer.isVisible = false
        binding.videoPlayer.isVisible = true

        binding.detailsDuration.apply {
            text = formatDurationLabel(video.duration)
            isVisible = video.duration != null && video.duration > 0
        }

        playerManager.initialize(binding.videoPlayer)
        playerManager.prepare(video.link)
    }

    private fun formatDurationLabel(duration: Int?): String {
        if (duration == null || duration <= 0) return ""
        return getString(
            R.string.workout_details_duration_label,
            DurationFormatter.format(duration, resources)
        )
    }

    private fun showClientError() {
        binding.videoLoadingProgress.isVisible = false
        binding.videoPlayer.isVisible = false
        binding.videoErrorContainer.isVisible = true
        binding.videoErrorText.setText(R.string.video_load_client_error)
        binding.retryVideoButton.isVisible = false
    }

    private fun showServerError() {
        binding.retryVideoButton.setOnClickListener { viewModel.retryLoadVideo() }

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
        const val INVALID_WORKOUT_ID = -1

        fun newInstance() = WorkoutDetailsFragment()
    }
}
