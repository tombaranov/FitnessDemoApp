package tombaranov.fitnessdemoapp.workouts.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tombaranov.fitnessdemoapp.R

class WorkoutAdapter : ListAdapter<WorkoutUiModel, WorkoutAdapter.WorkoutViewHolder>(DiffCallback) {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.workoutTitle)

        fun bindTitle(title: String) {
            titleView.text = title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.workout_item, parent, false)

        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: WorkoutViewHolder,
        position: Int,
    ) {
        val workout = getItem(position)

        holder.bindTitle(workout.title)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<WorkoutUiModel>() {
        override fun areItemsTheSame(oldItem: WorkoutUiModel, newItem: WorkoutUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WorkoutUiModel, newItem: WorkoutUiModel): Boolean =
            oldItem == newItem
    }
}