package tombaranov.fitnessdemoapp.workouts.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.workouts.data.WorkoutsRepositoryImpl
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutsViewModel

val workoutsModule = module {

    factoryOf(::WorkoutsInteractor)

    singleOf(::WorkoutsRepositoryImpl) bind WorkoutsRepository::class

    viewModelOf(::WorkoutsViewModel)
}