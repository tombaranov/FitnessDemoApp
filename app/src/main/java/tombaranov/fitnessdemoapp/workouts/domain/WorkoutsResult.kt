package tombaranov.fitnessdemoapp.workouts.domain

sealed class WorkoutsResult {
    data class Success(val workouts: List<Workout>) : WorkoutsResult()
    object ClientError : WorkoutsResult()
    object ServerError : WorkoutsResult()
}
