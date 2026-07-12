package tombaranov.fitnessdemoapp.player

sealed interface PlayerEvent {
    object Error : PlayerEvent
    data class TracksChanged(val tracks: List<VideoTrack>) : PlayerEvent
}
