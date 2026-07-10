package tombaranov.fitnessdemoapp.workouts.domain

import tombaranov.fitnessdemoapp.core.network.Response

interface WorkoutsRepository {

    suspend fun loadAll(): Response<List<Workout>>
}
