package tombaranov.fitnessdemoapp.workouts.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
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
import tombaranov.fitnessdemoapp.workouts.domain.Type
import tombaranov.fitnessdemoapp.workouts.domain.toUiModel

// TODO: Провести рефакторинг после завершения работы
class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private lateinit var binding: FragmentWorkoutsBinding

    private val viewModel: WorkoutsViewModel by viewModel()
    private val workoutAdapter: WorkoutAdapter = WorkoutAdapter(
        onItemClick = { openDetailsScreen(it) }
    )

    override fun onResume() {
        super.onResume()

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroyView() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkoutsBinding.bind(view)

        observeUiState()
        setupSearch()
        setupFilters()
        setupRecyclerView()

        viewModel.loadWorkouts()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        WorkoutsUiState.Loading -> showLoading()
                        is WorkoutsUiState.Loaded -> showWorkouts(state)
                        WorkoutsUiState.ClientError -> showClientError()
                        WorkoutsUiState.ServerError -> showServerError()
                    }
                }
            }
        }
    }

    private fun setupSearch() {
        binding.workoutsSearchInput.doOnTextChanged { newQuery, _, _, _ ->
            viewModel.onSearchQueryChanged(query = newQuery.toString())
        }
    }

    private fun setupFilters() {
        binding.resetFilters.setOnClickListener {
            binding.workoutsFilterContainer.clearCheck()
            viewModel.resetFilters()
        }

        binding.workoutsFilterContainer.setOnCheckedStateChangeListener { _, checkedIds ->
            val type = when (checkedIds.firstOrNull()) {
                R.id.filterWorkout -> Type.Workout
                R.id.filterComplex -> Type.Complex
                R.id.filterLive -> Type.Live
                else -> null
            }

            viewModel.onTypeFilterSelected(type)
        }
    }

    private fun setupRecyclerView() {
        binding.workoutsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.workoutsRecyclerView.adapter = workoutAdapter
    }

    private fun openDetailsScreen(workout: WorkoutUiModel) {
        val bundle = Bundle().apply {
            putInt(ARG_WORKOUT_ID, workout.id)
            putString(ARG_WORKOUT_TITLE, workout.title)
            putString(ARG_WORKOUT_TYPE, workout.typeName)
            putString(ARG_WORKOUT_DESCRIPTION, workout.description)
        }
        findNavController().navigate(
            R.id.action_workoutsFragment_to_workoutDetailsFragment, bundle
        )
    }

    private fun showLoading() {
        binding.workoutsRecyclerView.isVisible = false
        binding.workoutsErrorContainer.isVisible = false
        binding.workoutsLoadingProgress.isVisible = true
    }

    private fun showWorkouts(state: WorkoutsUiState.Loaded) {
        binding.workoutsLoadingProgress.isVisible = false
        binding.workoutsErrorContainer.isVisible = false
        binding.workoutsRecyclerView.isVisible = true

        if (binding.workoutsSearchInput.text.toString() != state.searchQuery) {
            binding.workoutsSearchInput.setText(state.searchQuery)
        }

        val chipId = when (state.selectedType) {
            Type.Workout -> R.id.filterWorkout
            Type.Complex -> R.id.filterComplex
            Type.Live -> R.id.filterLive
            null -> View.NO_ID
        }
        binding.workoutsFilterContainer.check(chipId)

        workoutAdapter.submitList(state.workouts.map { workout ->
            workout.toUiModel(typeName = getString(workout.type.displayNameRes))
        })
    }

    private fun showClientError() {
        binding.workoutsLoadingProgress.isVisible = false
        binding.workoutsRecyclerView.isVisible = false
        binding.workoutsErrorContainer.isVisible = true
        binding.workoutsErrorText.setText(R.string.video_load_client_error)
        binding.workoutsRetryButton.isVisible = false
    }

    private fun showServerError() {
        binding.workoutsRetryButton.setOnClickListener { viewModel.loadWorkouts() }

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
        const val ARG_WORKOUT_DESCRIPTION = "workoutDescription"
    }
}
