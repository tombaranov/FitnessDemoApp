package tombaranov.fitnessdemoapp.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface PlayerEvent {
    object Error : PlayerEvent
}

data class VideoTrack(
    val trackIndex: Int,
    val id: String,
    val label: String,
    val width: Int,
    val height: Int,
    val bitrate: Int,
    val isSelected: Boolean,
)

class VideoPlayerManager(
    private val context: Context,
) {

    private var player: ExoPlayer? = null
    private var lastVideoUrl: String? = null
    private var savedPosition: Long = 0L

    private val _events = MutableSharedFlow<PlayerEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PlayerEvent> = _events.asSharedFlow()

    private val _videoTracks = MutableStateFlow<List<VideoTrack>>(emptyList())
    val videoTracks: StateFlow<List<VideoTrack>> = _videoTracks.asStateFlow()

    @OptIn(UnstableApi::class)
    fun initialize(playerView: PlayerView) {
        if (player != null) {
            playerView.player = player
            return
        }
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

                    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                        updateTracks(tracks)
                    }
                })
            }

        playerView.player = player
    }

    private fun updateTracks(tracks: androidx.media3.common.Tracks) {
        _videoTracks.value = tracks.groups
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
    }

    private fun buildVideoTrackLabel(width: Int, height: Int, bitrate: Int, label: String?): String {
        val resolution = if (height > 0) "${height}p" else null
        val bitrateStr = if (bitrate > 0) "${bitrate / 1_000_000} Mbps" else null
        return listOfNotNull(resolution, bitrateStr, label)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" — ")
            ?: label ?: "Unknown"
    }

    fun selectVideoTrack(track: VideoTrack) {
        applyTrackOverride(C.TRACK_TYPE_VIDEO, track.trackIndex)
    }

    fun clearVideoTrackSelection() {
        clearTrackOverride(C.TRACK_TYPE_VIDEO)
    }

    private fun applyTrackOverride(trackType: Int, trackIndex: Int) {
        val player = this.player ?: return

        val trackGroups = player.currentTracks.groups
            .filter { it.type == trackType }
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

    private fun clearTrackOverride(trackType: Int) {
        val player = this.player ?: return

        player.trackSelectionParameters = player.trackSelectionParameters
            .buildUpon()
            .clearOverridesOfType(trackType)
            .build()
    }

    fun prepare(videoUrl: String) {
        val isNewVideo = videoUrl != lastVideoUrl

        lastVideoUrl = videoUrl

        if (isNewVideo) {
            _videoTracks.value = emptyList()
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

    fun savePlaybackState() {
        savedPosition = player?.currentPosition ?: 0L
        player?.playWhenReady = true
    }

    fun release() {
        savedPosition = 0L
        lastVideoUrl = ""
        _videoTracks.value = emptyList()
        player?.playWhenReady = false
        player?.release()
        player = null
    }
}
