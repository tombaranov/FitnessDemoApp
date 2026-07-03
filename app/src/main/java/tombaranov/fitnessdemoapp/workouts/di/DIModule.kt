package tombaranov.fitnessdemoapp.workouts.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.workouts.data.WorkoutsRepositoryImpl
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository

val workoutsModule = module {
    singleOf(::WorkoutsRepositoryImpl) bind WorkoutsRepository::class
}