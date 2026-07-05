package tombaranov.fitnessdemoapp.workouts.di

import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.core.coroutines.DispatchersQualifiers
import tombaranov.fitnessdemoapp.workouts.data.FakeWorkoutsRepository
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsInteractor
import tombaranov.fitnessdemoapp.workouts.domain.WorkoutsRepository
import tombaranov.fitnessdemoapp.workouts.presentation.WorkoutsViewModel

val workoutsModule = module {

    factoryOf(::WorkoutsInteractor)

    // TODO: Заменить на WorkoutsRepositoryImpl после реализации сетвого слоя
    singleOf(::FakeWorkoutsRepository) bind WorkoutsRepository::class

    viewModel {
        WorkoutsViewModel(
            workoutsInteractor = get(),
            ioDispatcher = get(named(DispatchersQualifiers.IO))
        )
    }
}
