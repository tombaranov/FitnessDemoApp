package tombaranov.fitnessdemoapp.player.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.player.VideoPlayerManager

val playerModule = module {
    singleOf(::VideoPlayerManager)
}
