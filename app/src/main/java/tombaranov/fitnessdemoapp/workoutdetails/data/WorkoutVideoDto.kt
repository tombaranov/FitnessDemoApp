package tombaranov.fitnessdemoapp.workoutdetails.data

import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo

data class WorkoutVideoDto(
    val id: Int?,
    val duration: String?,
    val link: String?,
)

fun WorkoutVideoDto.toDomain(): WorkoutVideo? {
    return WorkoutVideo(
        id = this.id ?: return null,
        duration = this.duration ?: return null,
        link = this.link ?: return null
    )
}
