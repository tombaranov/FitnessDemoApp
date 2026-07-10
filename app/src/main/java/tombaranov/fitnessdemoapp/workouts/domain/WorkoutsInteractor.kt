package tombaranov.fitnessdemoapp.workouts.domain

import tombaranov.fitnessdemoapp.core.network.Response

class WorkoutsInteractor(
    private val workoutsRepository: WorkoutsRepository,
) {

    suspend fun loadAll(): WorkoutsResult {
        return when (val response = workoutsRepository.loadAll()) {
            is Response.Success -> WorkoutsResult.Success(workouts = response.data)
            is Response.Failure -> {
                if (response.isClientError) {
                    WorkoutsResult.ClientError
                } else {
                    WorkoutsResult.ServerError
                }
            }
        }
    }
}
