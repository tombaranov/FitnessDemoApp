package tombaranov.fitnessdemoapp.player.mappers

import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import tombaranov.fitnessdemoapp.player.tracks.VideoTrack

class VideoTrackProvider {

    fun getVideTracksFrom(tracks: Tracks): List<VideoTrack> {
        val videoTrackGroups = tracks.groups
            .filter { it.type == C.TRACK_TYPE_VIDEO }

        return videoTrackGroups.flatMap { group ->
            val trackIndices = 0 until group.length
            trackIndices.map { trackIndex -> mapTrack(group, trackIndex) }
        }
    }

    @OptIn(UnstableApi::class)
    private fun mapTrack(
        group: Tracks.Group,
        trackIndex: Int,
    ): VideoTrack {
        val format = group.getTrackFormat(trackIndex)

        return VideoTrack(
            trackIndex = trackIndex,
            id = format.id ?: trackIndex.toString(),
            label = buildLabel(
                height = format.height,
                width = format.width
            ),
            width = format.width,
            height = format.height,
            bitrate = format.bitrate,
            isSelected = group.isSelected && group.isTrackSelected(trackIndex),
        )
    }

    private fun buildLabel(height: Int, width: Int): String {
        return "$width x $height"
    }
}
