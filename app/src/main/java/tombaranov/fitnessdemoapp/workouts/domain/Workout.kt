package tombaranov.fitnessdemoapp.workouts.domain

data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Type,
    val duration: Int,
)

enum class Type {
    Workout, Live, Complex
}
