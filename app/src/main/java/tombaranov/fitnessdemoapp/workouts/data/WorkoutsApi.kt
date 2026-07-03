package tombaranov.fitnessdemoapp.workouts.data

interface WorkoutsApi {

    suspend fun loadAll(): List<WorkoutDto>
}
