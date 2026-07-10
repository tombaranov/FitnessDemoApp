package tombaranov.fitnessdemoapp.workouts.domain

import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutUiModel

data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Type,
    val duration: Int?,
)

enum class Type(val displayNameRes: Int) {
    Workout(R.string.workouts_screen_workout_type_training),
    Live(R.string.workouts_screen_workout_type_live),
    Complex(R.string.workouts_screen_workout_type_complex)
}

fun Workout.toUiModel(typeName: String): WorkoutUiModel {
    return WorkoutUiModel(
        id = this.id,
        title = this.title,
        description = this.description,
        typeRes = this.type.displayNameRes,
        typeName = typeName,
        duration = this.duration,
    )
}
