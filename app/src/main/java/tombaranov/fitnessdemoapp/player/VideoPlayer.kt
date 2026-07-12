package tombaranov.fitnessdemoapp.player

import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.SharedFlow

interface VideoPlayer {

    val events: SharedFlow<PlayerEvent>

    fun initialize(playerView: PlayerView)

    fun prepare(videoUrl: String)

    fun selectVideoTrack(track: VideoTrack)

    fun savePlaybackState()

    fun detach(playerView: PlayerView)
}
