package tombaranov.fitnessdemoapp.workoutdetails.data

import tombaranov.fitnessdemoapp.core.network.Response
import tombaranov.fitnessdemoapp.core.network.handleRequest
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoRepository

class WorkoutVideoRepositoryImpl(
    private val workoutVideoApi: WorkoutVideoApi,
) : WorkoutVideoRepository {

    override suspend fun loadWorkoutVideoBy(id: Int): Response<WorkoutVideo> {
        val response = handleRequest {
            workoutVideoApi.loadWorkoutVideoBy(id = id)
        }

        return when (response) {
            is Response.Success -> {
                val video = response.data.toDomain()

                if (video != null) {
                    Response.Success(data = video)
                } else {
                    Response.Failure.NotFound
                }
            }

            is Response.Failure -> response
        }
    }
}
