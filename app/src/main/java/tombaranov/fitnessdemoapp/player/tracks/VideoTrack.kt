package tombaranov.fitnessdemoapp.player.tracks

data class VideoTrack(
    val trackIndex: Int,
    val id: String,
    val label: String,
    val width: Int,
    val height: Int,
    val bitrate: Int,
    val isSelected: Boolean,
)