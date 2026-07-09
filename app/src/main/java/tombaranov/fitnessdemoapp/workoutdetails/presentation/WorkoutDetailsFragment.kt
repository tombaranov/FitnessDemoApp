package tombaranov.fitnessdemoapp.workoutdetails.presentation

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import tombaranov.fitnessdemoapp.R

class WorkoutDetailsFragment : Fragment() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    private val viewModel: WorkoutDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_workout_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView = view.findViewById(R.id.videoPlayer)
        initializePlayer()
    }

    @OptIn(UnstableApi::class)
    private fun initializePlayer() {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)

        player = ExoPlayer.Builder(requireContext())
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        val listener = object : Player.Listener {

            @OptIn(UnstableApi::class)
            override fun onPlayerError(error: PlaybackException) {
                val cause = error.cause
                if (cause is HttpDataSource.InvalidResponseCodeException) {
                    Log.e("Player", "HTTP code: ${cause.responseCode}")
                    Log.e("Player", "Headers: ${cause.headerFields}")
                }
            }
        }

        player?.addListener(listener)
        playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse("https://ref.test.kolsa.ru/example-01.mp4"))

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
        player?.play()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.playWhenReady = false
        player?.release()
        player = null
    }

    companion object {
        fun newInstance() = WorkoutDetailsFragment()
    }
}
