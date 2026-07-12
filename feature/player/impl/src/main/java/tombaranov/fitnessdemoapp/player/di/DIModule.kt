package tombaranov.fitnessdemoapp.player.di

import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import tombaranov.fitnessdemoapp.player.factory.ExoPlayerFactory
import tombaranov.fitnessdemoapp.player.VideoPlayer
import tombaranov.fitnessdemoapp.player.VideoPlayerManager
import tombaranov.fitnessdemoapp.player.tracks.VideoTrackController
import tombaranov.fitnessdemoapp.player.mappers.VideoTrackProvider

val playerModule = module {
    singleOf(::ExoPlayerFactory)
    singleOf(::VideoTrackController)
    singleOf(::VideoTrackProvider)
    singleOf(::VideoPlayerManager) { bind<VideoPlayer>() }
}
