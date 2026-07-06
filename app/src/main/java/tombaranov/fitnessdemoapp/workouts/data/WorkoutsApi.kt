package tombaranov.fitnessdemoapp.workouts.data

import retrofit2.http.GET

interface WorkoutsApi {

    @GET("/get_workouts")
    suspend fun loadAll(): List<WorkoutDto>
}
