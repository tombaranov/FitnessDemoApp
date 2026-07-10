package tombaranov.fitnessdemoapp.workouts.presentation

import tombaranov.fitnessdemoapp.workouts.domain.Type
import tombaranov.fitnessdemoapp.workouts.domain.Workout

class SearchHelper {

    fun apply(
        workouts: List<Workout>,
        searchQuery: String,
        selectedFilterType: Type?
    ): List<Workout> {
        return workouts
            .filter { workout ->
                workout.title.contains(searchQuery, ignoreCase = true)
            }
            .filter { workout ->
                selectedFilterType == null || workout.type == selectedFilterType
            }
    }
}
