package tombaranov.fitnessdemoapp.workouts.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.databinding.FragmentWorkoutsBinding
import tombaranov.fitnessdemoapp.workoutdetails.presentation.DurationFormatter
import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.toUiModel

class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private lateinit var binding: FragmentWorkoutsBinding

    private val viewModel: WorkoutsViewModel by viewModel()
    private val workoutAdapter: WorkoutAdapter = WorkoutAdapter(
        onItemClick = { openDetailsScreen(it) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkoutsBinding.bind(view)

        binding.workoutsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.workoutsRecyclerView.adapter = workoutAdapter
        binding.workoutsRetryButton.setOnClickListener { viewModel.loadWorkouts() }

        observeUiState()
        viewModel.loadWorkouts()
    }

    private fun openDetailsScreen(workout: WorkoutUiModel) {
        val bundle = Bundle().apply {
            putInt(ARG_WORKOUT_ID, workout.id)
            putString(ARG_WORKOUT_TITLE, workout.title)
            putString(ARG_WORKOUT_TYPE, workout.typeName)
            putString(ARG_WORKOUT_DURATION, workout.duration)
            putString(ARG_WORKOUT_DESCRIPTION, workout.description)
        }
        findNavController().navigate(
            R.id.action_workoutsFragment_to_workoutDetailsFragment, bundle
        )
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        WorkoutsUiState.Loading -> showLoading()
                        is WorkoutsUiState.Loaded -> showWorkouts(state.workouts)
                        WorkoutsUiState.ClientError -> showClientError()
                        WorkoutsUiState.ServerError -> showServerError()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.workoutsRecyclerView.isVisible = false
        binding.workoutsErrorContainer.isVisible = false
        binding.workoutsLoadingProgress.isVisible = true
    }

    private fun showWorkouts(workouts: List<Workout>) {
        binding.workoutsLoadingProgress.isVisible = false
        binding.workoutsErrorContainer.isVisible = false
        binding.workoutsRecyclerView.isVisible = true
        workoutAdapter.submitList(workouts.map { workout ->
            workout.toUiModel(typeName = getString(workout.type.displayNameRes))
                .copy(duration = formatWorkoutDuration(workout.duration))
        })
    }

    private fun formatWorkoutDuration(rawDuration: String): String {
        val totalMinutes = rawDuration.toIntOrNull()
        return totalMinutes?.let { DurationFormatter.format(it, resources) } ?: rawDuration
    }

    private fun showClientError() {
        binding.workoutsLoadingProgress.isVisible = false
        binding.workoutsRecyclerView.isVisible = false
        binding.workoutsErrorContainer.isVisible = true
        binding.workoutsErrorText.setText(R.string.video_load_client_error)
        binding.workoutsRetryButton.isVisible = false
    }

    private fun showServerError() {
        binding.workoutsLoadingProgress.isVisible = false
        binding.workoutsRecyclerView.isVisible = false
        binding.workoutsErrorContainer.isVisible = true
        binding.workoutsErrorText.setText(R.string.workouts_error)
        binding.workoutsRetryButton.isVisible = true
    }

    companion object {
        const val ARG_WORKOUT_ID = "workoutId"
        const val ARG_WORKOUT_TITLE = "workoutTitle"
        const val ARG_WORKOUT_TYPE = "workoutType"
        const val ARG_WORKOUT_DURATION = "workoutDuration"
        const val ARG_WORKOUT_DESCRIPTION = "workoutDescription"

        fun newInstance() = WorkoutsFragment()
    }
}
