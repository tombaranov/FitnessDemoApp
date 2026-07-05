package tombaranov.fitnessdemoapp.workoutdetails.domain

class WorkoutVideoInteractor(
    private val workoutVideoRepository: WorkoutVideoRepository,
) {

    suspend fun loadWorkoutVideoBy(id: Int): WorkoutVideo? {
        val workoutVideo = workoutVideoRepository.loadWorkoutVideoBy(id = id)

        return workoutVideo
    }
}
