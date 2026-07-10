package tombaranov.fitnessdemoapp.workouts.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.workoutdetails.presentation.DurationFormatter

class WorkoutAdapter(
    private val onItemClick: (WorkoutUiModel) -> Unit
) : ListAdapter<WorkoutUiModel, WorkoutAdapter.WorkoutViewHolder>(DiffCallback) {

    class WorkoutViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.workoutTitle)
        private val typeView: TextView = view.findViewById(R.id.workoutType)
        private val durationView: TextView = view.findViewById(R.id.workoutDuration)

        fun bind(item: WorkoutUiModel, onItemClick: (WorkoutUiModel) -> Unit) {
            titleView.text = item.title
            typeView.text = item.typeName
            durationView.text = item.duration
                ?.let { DurationFormatter.format(it, itemView.resources) }
                .orEmpty()
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.workout_item, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<WorkoutUiModel>() {
        override fun areItemsTheSame(oldItem: WorkoutUiModel, newItem: WorkoutUiModel): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: WorkoutUiModel, newItem: WorkoutUiModel): Boolean =
            oldItem == newItem
    }
}
