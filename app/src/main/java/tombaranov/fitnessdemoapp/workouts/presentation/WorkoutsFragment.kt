package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import tombaranov.fitnessdemoapp.R

class WorkoutsFragment : Fragment(R.layout.fragment_workouts) {

    private val viewModel: WorkoutsViewModel by viewModels()

    companion object {
        fun newInstance() = WorkoutsFragment()
    }
}