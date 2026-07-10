package tombaranov.fitnessdemoapp.workouts.presentation

import androidx.annotation.StringRes

data class WorkoutUiModel(
    val id: Int,
    val title: String,
    val description: String?,
    @StringRes
    val typeRes: Int,
    val typeName: String,
    val duration: Int?,
)
