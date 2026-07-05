package tombaranov.fitnessdemoapp.workoutdetails.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.workoutdetails.data.WorkoutVideoRepositoryImpl
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoInteractor
import tombaranov.fitnessdemoapp.workoutdetails.domain.WorkoutVideoRepository
import tombaranov.fitnessdemoapp.workoutdetails.presentation.WorkoutDetailsViewModel

val workoutDetailsModule = module {
    factoryOf(::WorkoutVideoInteractor)

    singleOf(::WorkoutVideoRepositoryImpl) bind WorkoutVideoRepository::class

    viewModelOf(::WorkoutDetailsViewModel)
}