package tombaranov.fitnessdemoapp.workouts.data

import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val workoutsApi: WorkoutsApi,
) : WorkoutsRepository {

    override suspend fun loadAll(): List<Workout> {
        val workouts = workoutsApi.loadAll()

        return workouts.mapNotNull { it.toDomain() }
    }
}
