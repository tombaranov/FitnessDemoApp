package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.annotation.StringRes

data class WorkoutUiModel(
    val id: Int,
    val title: String,
    val description: String?,
    @StringRes
    val type: Int,
    val duration: String,
)
