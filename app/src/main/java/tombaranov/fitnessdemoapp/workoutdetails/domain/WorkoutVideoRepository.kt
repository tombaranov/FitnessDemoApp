package tombaranov.fitnessdemoapp.workoutdetails.domain

import tombaranov.fitnessdemoapp.core.network.Response

interface WorkoutVideoRepository {

    suspend fun loadWorkoutVideoBy(id: Int): Response<WorkoutVideo>
}
