package tombaranov.fitnessdemoapp.workouts.data

import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.Type

data class WorkoutDto(
    val id: Int?,
    val title: String?,
    val description: String?,
    val type: Int?,
    val duration: Int?,
)

fun WorkoutDto.toDomain(): Workout? {
    val workoutType = when (this.type) {
        1 -> Type.Workout
        2 -> Type.Complex
        3 -> Type.Live
        else -> return null
    }

    return Workout(
        id = this.id ?: return null,
        title = this.title ?: return null,
        description = this.description,
        type = workoutType,
        duration = this.duration ?: return null
    )
}

