package tombaranov.fitnessdemoapp.workoutdetails.domain

import tombaranov.fitnessdemoapp.core.network.Response

class WorkoutVideoInteractor(
    private val workoutVideoRepository: WorkoutVideoRepository,
) {

    suspend fun loadWorkoutVideoBy(id: Int): WorkoutVideoResult {
        val response = workoutVideoRepository.loadWorkoutVideoBy(id = id)

        return when (response) {
            is Response.Success -> WorkoutVideoResult.Success(video = response.data)
            is Response.Failure -> {
                if (response.isClientError) {
                    WorkoutVideoResult.ClientError
                } else {
                    WorkoutVideoResult.ServerError
                }
            }
        }
    }
}

