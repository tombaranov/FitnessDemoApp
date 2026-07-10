package tombaranov.fitnessdemoapp.player.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.player.VideoPlayerManager

val playerModule = module {
    factoryOf(::VideoPlayerManager)
}
