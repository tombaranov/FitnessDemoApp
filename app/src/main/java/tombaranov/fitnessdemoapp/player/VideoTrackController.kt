package tombaranov.fitnessdemoapp.player

import androidx.media3.common.C
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.exoplayer.ExoPlayer

class VideoTrackController {

    fun applyTrackOverride(player: ExoPlayer, trackIndex: Int) {
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
}
