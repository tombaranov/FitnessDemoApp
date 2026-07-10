package tombaranov.fitnessdemoapp.workouts.presentation

import tombaranov.fitnessdemoapp.workouts.domain.Type
import tombaranov.fitnessdemoapp.workouts.domain.Workout

class SearchHelper {

    fun apply(
        workouts: List<Workout>,
        selectedFilterType: Type?,
        searchQuery: String = "",
    ): List<Workout> {
        return workouts
            .filter { workout ->
                searchQuery.isBlank() || workout.title.contains(searchQuery, ignoreCase = true)
            }
            .filter { workout ->
                selectedFilterType == null || workout.type == selectedFilterType
            }
    }
}
