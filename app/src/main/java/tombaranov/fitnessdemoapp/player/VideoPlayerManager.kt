package tombaranov.fitnessdemoapp.player

import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class VideoPlayerManager(
    private val exoPlayerFactory: ExoPlayerFactory,
) : VideoPlayer {

    private var player: ExoPlayer? = null
    private var lastVideoUrl: String? = null
    private var savedPosition: Long = 0L

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 2)
    override val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            Log.e("Player", "Playback error: ${error.message}", error)
            _events.tryEmit(PlayerEvent.Error)
        }

        override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
            updateTracks(tracks)
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

    @OptIn(UnstableApi::class)
    private fun updateTracks(tracks: androidx.media3.common.Tracks) {
        val videoTracks = tracks.groups
            .filter { it.type == C.TRACK_TYPE_VIDEO }
            .flatMap { group ->
                (0 until group.length).map { trackIndex ->
                    val format = group.getTrackFormat(trackIndex)
                    VideoTrack(
                        trackIndex = trackIndex,
                        id = format.id ?: trackIndex.toString(),
                        label = buildVideoTrackLabel(format.width, format.height, format.bitrate, format.label),
                        width = format.width,
                        height = format.height,
                        bitrate = format.bitrate,
                        isSelected = group.isSelected && group.isTrackSelected(trackIndex),
                    )
                }
            }

        _events.tryEmit(PlayerEvent.TracksChanged(videoTracks))
    }

    private fun buildVideoTrackLabel(width: Int, height: Int, bitrate: Int, label: String?): String {
        val resolution = if (height > 0) "${height}p" else null
        val bitrateStr = if (bitrate > 0) "${bitrate / 1_000_000} Mbps" else null
        return listOfNotNull(resolution, bitrateStr, label)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" — ")
            ?: label ?: "Unknown"
    }

    override fun selectVideoTrack(track: VideoTrack) {
        applyTrackOverride(track.trackIndex)
    }

    private fun applyTrackOverride(trackIndex: Int) {
        val player = this.player ?: return

        val trackGroups = player.currentTracks.groups
            .filter { it.type == C.TRACK_TYPE_VIDEO }
            .firstOrNull()
            ?: return

        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .setOverrideForType(
                TrackSelectionOverride(
                    trackGroups.mediaTrackGroup,
                    trackIndex,
                )
            )
            .build()
    }

    override fun prepare(videoUrl: String) {
        val isNewVideo = videoUrl != lastVideoUrl

        lastVideoUrl = videoUrl

        if (isNewVideo) {
            player?.setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            player?.prepare()
            player?.playWhenReady = true
        } else {
            if (savedPosition > 0L) {
                player?.seekTo(savedPosition)
            }
            player?.playWhenReady = false
        }
    }

    override fun savePlaybackState() {
        savedPosition = player?.currentPosition ?: 0L
        player?.playWhenReady = true
    }

    override fun detach(playerView: PlayerView) {
        playerView.player = null
    }

    fun release() {
        savedPosition = 0L
        lastVideoUrl = ""
        player?.playWhenReady = false
        player?.release()
        player = null
    }
}
