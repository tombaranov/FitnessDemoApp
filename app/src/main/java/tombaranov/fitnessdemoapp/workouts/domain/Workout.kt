package tombaranov.fitnessdemoapp.workouts.domain

import tombaranov.fitnessdemoapp.R
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutUiModel

data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Type,
    val duration: Int,
)

enum class Type {
    Workout, Live, Complex
}

fun Workout.toUiModel(): WorkoutUiModel {

    val type = when (this.type) {
        Type.Workout -> R.string.workouts_screen_workut_type_training
        Type.Live -> R.string.workouts_screen_workut_type_live
        Type.Complex -> R.string.workouts_screen_workut_type_complex
    }

    return WorkoutUiModel(
        id = this.id,
        title = this.title,
        description = this.description,
        type = type,
        duration = this.duration,
    )
}
