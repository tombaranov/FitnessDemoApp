package tombaranov.fitnessdemoapp.workoutdetails.data

import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutVideoApi {

    @GET("/get_video")
    suspend fun loadWorkoutVideoBy(@Query("id") id: Int): WorkoutVideoDto
}
