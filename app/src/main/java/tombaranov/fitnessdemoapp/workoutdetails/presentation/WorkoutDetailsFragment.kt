package tombaranov.fitnessdemoapp.workoutdetails.presentation

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tombaranov.fitnessdemoapp.R

class WorkoutDetailsFragment : Fragment() {

    private val viewModel: WorkoutDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workout_details, container, false)
    }

    companion object {
        fun newInstance() = WorkoutDetailsFragment()
    }
}
