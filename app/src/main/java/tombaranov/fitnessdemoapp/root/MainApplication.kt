package tombaranov.fitnessdemoapp.root

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import tombaranov.fitnessdemoapp.core.coroutines.dispatchersModule
import tombaranov.fitnessdemoapp.workouts.di.workoutsModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MainApplication)
            modules(dispatchersModule, workoutsModule)
        }
    }
}