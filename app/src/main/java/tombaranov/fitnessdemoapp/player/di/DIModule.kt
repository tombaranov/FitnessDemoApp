package tombaranov.fitnessdemoapp.player.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.player.ExoPlayerFactory
import tombaranov.fitnessdemoapp.player.VideoPlayer
import tombaranov.fitnessdemoapp.player.VideoPlayerManager
import tombaranov.fitnessdemoapp.player.VideoTrackController
import tombaranov.fitnessdemoapp.player.mappers.VideoTrackMapper

val playerModule = module {
    singleOf(::ExoPlayerFactory)
    singleOf(::VideoTrackController)
    singleOf(::VideoTrackMapper)
    singleOf(::VideoPlayerManager) { bind<VideoPlayer>() }
}
