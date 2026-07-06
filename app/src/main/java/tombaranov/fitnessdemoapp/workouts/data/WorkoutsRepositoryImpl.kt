package tombaranov.fitnessdemoapp.workouts.data

import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val workoutsApi: WorkoutsApi,
) : WorkoutsRepository {

    override suspend fun loadAll(): List<Workout> {
        return workoutsApi.loadAll().mapNotNull { it.toDomain() }
    }
}
