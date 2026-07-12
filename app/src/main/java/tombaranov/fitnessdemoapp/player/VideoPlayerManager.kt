package tombaranov.fitnessdemoapp.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import tombaranov.fitnessdemoapp.player.mappers.VideoTrackMapper

class VideoPlayerManager(
    private val exoPlayerFactory: ExoPlayerFactory,
    private val trackController: VideoTrackController,
    private val videoTrackMapper: VideoTrackMapper,
) : VideoPlayer {

    private var player: ExoPlayer? = null
    private var playbackState: PlaybackState? = null

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 2)
    override val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.e("Player", "Playback error: ${error.message}", error)
            _events.tryEmit(PlayerEvent.Error)
        }

        override fun onTracksChanged(tracks: Tracks) {
            val videoTracks = videoTrackMapper.mapTracks(tracks)
            _events.tryEmit(PlayerEvent.TracksChanged(videoTracks))
        }
    }

    @OptIn(UnstableApi::class)
    override fun initialize(playerView: PlayerView) {
        if (player != null) {
            playerView.player = player
            return
        }

        player = exoPlayerFactory.create(playerListener)
        playerView.player = player
    }

    override fun selectVideoTrack(track: VideoTrack) {
        val player = player ?: return
        trackController.applyTrackOverride(player, track.trackIndex)
    }

    override fun prepare(videoUrl: String) {
        val isNewVideo = videoUrl != playbackState?.url
        val position = if (isNewVideo) START_PLAYBACK_POSITION else playbackState?.position
            ?: START_PLAYBACK_POSITION

        playbackState = PlaybackState(url = videoUrl, position = position)

        if (isNewVideo) {
            player?.setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            player?.prepare()
            player?.playWhenReady = true
        } else {
            if (position > START_PLAYBACK_POSITION) {
                player?.seekTo(position)
            }
            player?.playWhenReady = false
        }
    }

    override fun savePlaybackState() {
        val currentPosition = player?.currentPosition ?: START_PLAYBACK_POSITION
        playbackState = playbackState?.copy(position = currentPosition)
        player?.playWhenReady = true
    }

    override fun detach(playerView: PlayerView) {
        playerView.player = null
    }

    fun release() {
        playbackState = null
        player?.playWhenReady = false
        player?.release()
        player = null
    }

    private data class PlaybackState(
        val url: String,
        val position: Long = START_PLAYBACK_POSITION,
    )

    companion object {
        private const val START_PLAYBACK_POSITION = 0L
    }
}