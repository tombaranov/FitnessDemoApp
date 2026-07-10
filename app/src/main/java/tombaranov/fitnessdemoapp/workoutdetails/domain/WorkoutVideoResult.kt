package tombaranov.fitnessdemoapp.workoutdetails.domain

sealed class WorkoutVideoResult {
    data class Success(val video: WorkoutVideo) : WorkoutVideoResult()
    object ClientError : WorkoutVideoResult()
    object ServerError : WorkoutVideoResult()
}
