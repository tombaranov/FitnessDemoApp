package tombaranov.fitnessdemoapp.player

import tombaranov.fitnessdemoapp.player.tracks.VideoTrack

sealed interface PlayerEvent {
    object Error : PlayerEvent
    data class TracksChanged(val tracks: List<VideoTrack>) : PlayerEvent
}
