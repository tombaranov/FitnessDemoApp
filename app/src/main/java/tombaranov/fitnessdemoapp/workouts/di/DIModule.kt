package tombaranov.fitnessdemoapp.workouts.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import tombaranov.fitnessdemoapp.core.coroutines.DispatchersQualifiers
import tombaranov.fitnessdemoapp.workouts.data.WorkoutsApi
import tombaranov.fitnessdemoapp.workouts.data.WorkoutsRepositoryImpl
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository
import tombaranov.fitnessdemoapp.workouts.presentation.SearchHelper
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutsViewModel

val workoutsModule = module {

    factoryOf(::SearchHelper)

    factoryOf(::WorkoutsInteractor)

    singleOf(::WorkoutsRepositoryImpl) bind WorkoutsRepository::class

    single { get<Retrofit>().create(WorkoutsApi::class.java) }

    viewModel {
        WorkoutsViewModel(
            workoutsInteractor = get(),
            ioDispatcher = get(named(DispatchersQualifiers.IO)),
            searchHelper = get(),
        )
    }
}
