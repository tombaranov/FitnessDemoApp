package fitnessdemoapp.core.coroutines

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

object DispatchersQualifiers {
    const val IO = "IO"
    const val MAIN = "MAIN"
    const val DEFAULT = "DEFAULT"
}


val dispatchersModule = module {
    single(named(DispatchersQualifiers.IO)) { Dispatchers.IO }

    single(named(DispatchersQualifiers.DEFAULT)) { Dispatchers.Default }

    single(named(DispatchersQualifiers.MAIN)) { Dispatchers.Main }
}
