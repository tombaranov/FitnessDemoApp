package tombaranov.fitnessdemoapp.workoutdetails.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.workoutdetails.presentation.WorkoutDetailsViewModel

val workoutDetailsModule = module {

    viewModelOf(::WorkoutDetailsViewModel)
}