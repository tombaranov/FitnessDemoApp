package tombaranov.fitnessdemoapp.workouts.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.databinding.FragmentWorkoutsBinding

class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private lateinit var binding: FragmentWorkoutsBinding

    private val viewModel: WorkoutsViewModel by viewModels()
    private val workoutAdapter: WorkoutAdapter = WorkoutAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupWorkoutsRecyclerView(view = view)
        observeUiState()
    }

    private fun updateUiState(uiState: WorkoutsUiState) {
        when (uiState) {
            WorkoutsUiState.Loading -> {
            }

            is WorkoutsUiState.Loaded -> {
                workoutAdapter.submitList(uiState.workouts)
            }

            WorkoutsUiState.LoadingFailed -> {

            }
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    updateUiState(uiState = uiState)
                }
            }
        }
    }

    private fun setupWorkoutsRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.workoutsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = workoutAdapter
    }

    companion object {
        fun newInstance() = WorkoutsFragment()
    }
}
