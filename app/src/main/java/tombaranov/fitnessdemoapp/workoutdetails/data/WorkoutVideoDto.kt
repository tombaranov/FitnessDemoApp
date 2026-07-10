package tombaranov.fitnessdemoapp.workoutdetails.data

import tombaranov.fitnessdemoapp.core.network.ApiConfig
import tombaranov.fitnessdemoapp.core.network.HTTP_SCHEME
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo

data class WorkoutVideoDto(
    val id: Int?,
    val duration: String?,
    val link: String?,
)

fun WorkoutVideoDto.toDomain(): WorkoutVideo? {
    val videoLink = this.link ?: return null

    return WorkoutVideo(
        id = this.id ?: return null,
        duration = this.duration?.toIntOrNull(),
        link = if (videoLink.startsWith(HTTP_SCHEME)) videoLink else ApiConfig.BASE_URL + videoLink,
    )
}
