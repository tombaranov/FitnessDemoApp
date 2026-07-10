package tombaranov.fitnessdemoapp.workouts.data

import tombaranov.fitnessdemoapp.core.network.Response
import tombaranov.fitnessdemoapp.core.network.handleRequest
import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val workoutsApi: WorkoutsApi,
) : WorkoutsRepository {

    override suspend fun loadAll(): Response<List<Workout>> {
        val response = handleRequest {
            workoutsApi.loadAll()
        }
        return when (response) {
            is Response.Success -> {
                val workouts = response.data.mapNotNull { it.toDomain() }
                Response.Success(workouts)
            }
            is Response.Failure -> response
        }
    }
}
