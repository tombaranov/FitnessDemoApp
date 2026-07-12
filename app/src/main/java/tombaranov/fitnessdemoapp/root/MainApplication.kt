package tombaranov.fitnessdemoapp.root

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import fitnessdemoapp.core.coroutines.dispatchersModule
import tombaranov.fitnessdemoapp.core.network.networkModule
import tombaranov.fitnessdemoapp.player.di.playerModule
import tombaranov.fitnessdemoapp.workoutdetails.di.workoutDetailsModule
import tombaranov.fitnessdemoapp.workouts.di.workoutsModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dispatchersModule, networkModule, playerModule, workoutsModule, workoutDetailsModule)
        }
    }
}
