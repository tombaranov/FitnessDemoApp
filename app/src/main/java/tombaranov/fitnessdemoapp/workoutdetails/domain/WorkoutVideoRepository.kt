package tombaranov.fitnessdemoapp.workoutdetails.domain

interface WorkoutVideoRepository {

    suspend fun loadWorkoutVideoBy(id: Int): WorkoutVideo?
}
