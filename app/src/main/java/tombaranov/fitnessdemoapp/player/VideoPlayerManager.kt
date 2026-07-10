package tombaranov.fitnessdemoapp.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed interface PlayerEvent {
    object Error : PlayerEvent
}

class VideoPlayerManager(
    private val context: Context,
) {

    private var player: ExoPlayer? = null

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    @OptIn(UnstableApi::class)
    fun attachTo(playerView: PlayerView) {
        release()
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        player = ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
            .also { exoPlayer ->
                exoPlayer.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("Player", "Playback error: ${error.message}", error)
                        _events.tryEmit(PlayerEvent.Error)
                    }
                })
            }

        playerView.player = player
    }

    fun play(videoUrl: String) {
        val mediaItem = MediaItem.fromUri(videoUrl.toUri())
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    fun release() {
        player?.playWhenReady = false
        player?.release()
        player = null
    }
}
