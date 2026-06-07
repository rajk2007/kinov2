package com.rajkarmakar.kino.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.ExoPlayer
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.rajkarmakar.kino.data.model.ContentType
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.data.model.PlayerState
import com.rajkarmakar.kino.data.repository.MediaRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PlayerViewModel constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState

    private val _media = MutableStateFlow<MediaItem?>(null)
    val media: StateFlow<MediaItem?> = _media

    private val _showControls = MutableStateFlow(true)
    val showControls: StateFlow<Boolean> = _showControls

    private val _isFullscreen = MutableStateFlow(true)
    val isFullscreen: StateFlow<Boolean> = _isFullscreen

    private val _showSkipIntro = MutableStateFlow(false)
    val showSkipIntro: StateFlow<Boolean> = _showSkipIntro

    private val _showSkipOutro = MutableStateFlow(false)
    val showSkipOutro: StateFlow<Boolean> = _showSkipOutro

    private val _showNextEpisode = MutableStateFlow(false)
    val showNextEpisode: StateFlow<Boolean> = _showNextEpisode

    private val _brightness = MutableStateFlow(0.5f)
    val brightness: StateFlow<Float> = _brightness

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume

    private val _showBrightnessIndicator = MutableStateFlow(false)
    val showBrightnessIndicator: StateFlow<Boolean> = _showBrightnessIndicator

    private val _showVolumeIndicator = MutableStateFlow(false)
    val showVolumeIndicator: StateFlow<Boolean> = _showVolumeIndicator

    private val _showSeekIndicator = MutableStateFlow(false)
    val showSeekIndicator: StateFlow<Boolean> = _showSeekIndicator

    private val _seekPosition = MutableStateFlow(0L)
    val seekPosition: StateFlow<Long> = _seekPosition

    private val _isLocked = MutableStateFlow(false)
    val isLocked: StateFlow<Boolean> = _isLocked

    private var exoPlayer: ExoPlayer? = null
    private var controlsJob: Job? = null
    private var currentEpisodeIndex = 0
    var currentEpisodeNumber = 1

    fun setExoPlayer(player: ExoPlayer) {
        exoPlayer = player
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                _playerState.value = _playerState.value.copy(
                    isBuffering = playbackState == Player.STATE_BUFFERING
                )
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
            }
        })

        viewModelScope.launch {
            while (isActive) {
                exoPlayer?.let { exo ->
                    _playerState.value = _playerState.value.copy(
                        currentPosition = exo.currentPosition,
                        duration = exo.duration.coerceAtLeast(0),
                        bufferedPosition = exo.bufferedPosition
                    )

                    // Check skip intro/outro
                    checkSkipMarkers(exo.currentPosition)
                }
                delay(500)
            }
        }
    }

    fun loadMedia(mediaId: String, episodeId: String?) {
        viewModelScope.launch {
            try {
                val item = repository.getMediaDetails(mediaId)
                _media.value = item

                // Set up player
                val streamUrl = "https://example.com/stream/$mediaId"
                exoPlayer?.setMediaItem(ExoMediaItem.fromUri(streamUrl))
                exoPlayer?.prepare()
                exoPlayer?.play()

                startControlsTimer()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun checkSkipMarkers(position: Long) {
        val episode = _media.value?.episodes?.getOrNull(currentEpisodeIndex) ?: return

        episode.introStart?.let { start ->
            episode.introEnd?.let { end ->
                _showSkipIntro.value = position in start..end
            }
        }

        episode.outroStart?.let { start ->
            episode.outroEnd?.let { end ->
                _showSkipOutro.value = position in start..end
            }
        }

        // Show next episode near end
        val duration = _playerState.value.duration
        if (duration > 0 && position > duration - 60000) {
            _showNextEpisode.value = _media.value?.contentType != ContentType.MOVIE
        }
    }

    fun toggleControls() {
        _showControls.value = !_showControls.value
        if (_showControls.value) {
            startControlsTimer()
        } else {
            controlsJob?.cancel()
        }
    }

    private fun startControlsTimer() {
        controlsJob?.cancel()
        controlsJob = viewModelScope.launch {
            delay(4000)
            _showControls.value = false
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
        startControlsTimer()
    }

    fun seekBackward() {
        exoPlayer?.seekTo((exoPlayer?.currentPosition ?: 0) - 10000)
        startControlsTimer()
    }

    fun seekForward() {
        exoPlayer?.seekTo((exoPlayer?.currentPosition ?: 0) + 10000)
        startControlsTimer()
    }

    fun onHorizontalDrag(dragAmount: Float) {
        val seekAmount = (dragAmount * 50).toLong()
        val newPosition = (_seekPosition.value + seekAmount).coerceIn(0, _playerState.value.duration)
        _seekPosition.value = newPosition
        _showSeekIndicator.value = true
    }

    fun hideSeekIndicator() {
        _showSeekIndicator.value = false
        exoPlayer?.seekTo(_seekPosition.value)
    }

    fun onBrightnessDrag(dragAmount: Float) {
        _brightness.value = (_brightness.value - dragAmount * 0.01f).coerceIn(0f, 1f)
        _showBrightnessIndicator.value = true
    }

    fun hideBrightnessIndicator() {
        _showBrightnessIndicator.value = false
    }

    fun onVolumeDrag(dragAmount: Float) {
        _volume.value = (_volume.value - dragAmount * 0.01f).coerceIn(0f, 1f)
        _showVolumeIndicator.value = true
    }

    fun hideVolumeIndicator() {
        _showVolumeIndicator.value = false
    }

    fun onSeek(fraction: Float) {
        val position = (fraction * _playerState.value.duration).toLong()
        _seekPosition.value = position
    }

    fun onSeekComplete() {
        exoPlayer?.seekTo(_seekPosition.value)
    }

    fun skipIntro() {
        val episode = _media.value?.episodes?.getOrNull(currentEpisodeIndex)
        episode?.introEnd?.let { exoPlayer?.seekTo(it) }
        _showSkipIntro.value = false
    }

    fun skipOutro() {
        val episode = _media.value?.episodes?.getOrNull(currentEpisodeIndex)
        episode?.outroEnd?.let { exoPlayer?.seekTo(it) }
        _showSkipOutro.value = false
    }

    fun playNextEpisode() {
        val episodes = _media.value?.episodes ?: return
        if (currentEpisodeIndex < episodes.size - 1) {
            currentEpisodeIndex++
            currentEpisodeNumber = episodes[currentEpisodeIndex].episodeNumber
            loadMedia(_media.value!!.id, episodes[currentEpisodeIndex].id)
        }
        _showNextEpisode.value = false
    }

    fun playPreviousEpisode() {
        val episodes = _media.value?.episodes ?: return
        if (currentEpisodeIndex > 0) {
            currentEpisodeIndex--
            currentEpisodeNumber = episodes[currentEpisodeIndex].episodeNumber
            loadMedia(_media.value!!.id, episodes[currentEpisodeIndex].id)
        }
    }

    fun toggleFullscreen() {
        _isFullscreen.value = !_isFullscreen.value
    }

    fun toggleLock() {
        _isLocked.value = !_isLocked.value
    }

    fun enterPipMode() {
        // Implement PiP
    }

    fun showQualitySelector() {}
    fun showSpeedSelector() {}
    fun showAudioSelector() {}
    fun showSubtitleSelector() {}

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        controlsJob?.cancel()
    }
}
