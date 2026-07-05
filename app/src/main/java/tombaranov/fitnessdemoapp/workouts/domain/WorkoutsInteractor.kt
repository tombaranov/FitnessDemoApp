package tombaranov.fitnessdemoapp.workouts.domain

class WorkoutsInteractor(
    private val workoutsRepository: WorkoutsRepository,
) {

    suspend fun loadAll(): List<Workout> {
        val workouts = workoutsRepository.loadAll()

        return workouts
    }
}
