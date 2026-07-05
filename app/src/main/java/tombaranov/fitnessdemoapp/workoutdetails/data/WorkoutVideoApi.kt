package tombaranov.fitnessdemoapp.workoutdetails.data

interface WorkoutVideoApi {

    suspend fun loadWorkoutVideoBy(id: Int): WorkoutVideoDto
}
