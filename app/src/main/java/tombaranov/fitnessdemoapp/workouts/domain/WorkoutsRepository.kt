package tombaranov.fitnessdemoapp.workouts.domain

interface WorkoutsRepository {

    suspend fun loadAll(): List<Workout>
}
