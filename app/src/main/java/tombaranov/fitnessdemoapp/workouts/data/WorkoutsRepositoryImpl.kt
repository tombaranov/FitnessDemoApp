package tombaranov.fitnessdemoapp.workouts.data

import kotlinx.coroutines.delay
import tombaranov.fitnessdemoapp.workouts.domain.Type
import tombaranov.fitnessdemoapp.workouts.domain.Workout
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository

class WorkoutsRepositoryImpl(
    private val workoutsApi: WorkoutsApi,
) : WorkoutsRepository {

    override suspend fun loadAll(): List<Workout> {
        val workouts = workoutsApi.loadAll()

        return workouts.mapNotNull { it.toDomain() }
    }
}

// TODO: Удалить после реализации сетевого слоя
/**
 * Репозиторий-заглушка. Эмулирует задержку в 3 секунды перед получением данных
 */
class FakeWorkoutsRepository : WorkoutsRepository {

    private val fakeWorkouts = listOf(
        Workout(
            id = 1,
            title = "Заголовок 1",
            description = "Описание 1",
            type = Type.Workout,
            duration = 10
        ),
        Workout(
            id = 2,
            title = "Заголовок 2",
            description = "Описание 2",
            type = Type.Live,
            duration = 15
        ),
        Workout(
            id = 3,
            title = "Заголовок 3",
            description = "Описание 3",
            type = Type.Complex,
            duration = 20
        )
    )

    override suspend fun loadAll(): List<Workout> {
        val delayInMillis = 3_000L

        delay(delayInMillis)

        return fakeWorkouts
    }
}
