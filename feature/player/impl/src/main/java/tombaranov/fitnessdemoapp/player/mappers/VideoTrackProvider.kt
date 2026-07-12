package tombaranov.fitnessdemoapp.player.mappers

import androidx.media3.common.C
import androidx.media3.common.Tracks
import tombaranov.fitnessdemoapp.player.tracks.VideoTrack

class VideoTrackProvider {

    fun getVideTracksFrom(tracks: Tracks): List<VideoTrack> {
        return tracks.groups
            .filter { it.type == C.TRACK_TYPE_VIDEO }
            .flatMap { group ->
                (0 until group.length).map { trackIndex ->
                    val format = group.getTrackFormat(trackIndex)

                    VideoTrack(
                        trackIndex = trackIndex,
                        id = format.id ?: trackIndex.toString(),
                        label = buildLabel(
                            format.width, format.height, format.bitrate, format.label
                        ),
                        width = format.width,
                        height = format.height,
                        bitrate = format.bitrate,
                        isSelected = group.isSelected && group.isTrackSelected(trackIndex),
                    )
                }
            }
    }

    private fun buildLabel(width: Int, height: Int, bitrate: Int, label: String?): String {
        val resolution = if (height > 0) "${height}p" else null
        val bitrateStr = if (bitrate > 0) "${bitrate / 1_000_000} Mbps" else null
        return listOfNotNull(resolution, bitrateStr, label)
            .takeIf { it.isNotEmpty() }
            ?.joinToString(" — ")
            ?: label.orEmpty()
    }
}
