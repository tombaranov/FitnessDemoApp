package tombaranov.fitnessdemoapp.workoutdetails.data

import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideo
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoRepository

class WorkoutVideoRepositoryImpl(
    private val workoutVideoApi: WorkoutVideoApi,
) : WorkoutVideoRepository {

    override suspend fun loadWorkoutVideoBy(id: Int): WorkoutVideo? {
        val workout = workoutVideoApi.loadWorkoutVideoBy(id = id)

        return workout.toDomain()
    }
}
