package tombaranov.fitnessdemoapp.player.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.player.ExoPlayerFactory
import tombaranov.fitnessdemoapp.player.VideoPlayer
import tombaranov.fitnessdemoapp.player.VideoPlayerManager

val playerModule = module {
    singleOf(::ExoPlayerFactory)
    singleOf(::VideoPlayerManager) { bind<VideoPlayer>() }
}
