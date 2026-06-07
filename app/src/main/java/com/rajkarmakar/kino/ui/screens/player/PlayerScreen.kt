package com.rajkarmakar.kino.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.View
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem as ExoMediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.rajkarmakar.kino.data.model.ContentType
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    mediaId: String,
    episodeId: String?,
    onBackClick: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val playerState by viewModel.playerState.collectAsState()
    val media by viewModel.media.collectAsState()
    val showControls by viewModel.showControls.collectAsState()
    val isFullscreen by viewModel.isFullscreen.collectAsState()
    val showSkipIntro by viewModel.showSkipIntro.collectAsState()
    val showSkipOutro by viewModel.showSkipOutro.collectAsState()
    val showNextEpisode by viewModel.showNextEpisode.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val showBrightnessIndicator by viewModel.showBrightnessIndicator.collectAsState()
    val showVolumeIndicator by viewModel.showVolumeIndicator.collectAsState()
    val showSeekIndicator by viewModel.showSeekIndicator.collectAsState()
    val seekPosition by viewModel.seekPosition.collectAsState()

    // Lock orientation to landscape
    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Hide system UI
    DisposableEffect(Unit) {
        activity?.window?.decorView?.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )
        onDispose {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    LaunchedEffect(mediaId) {
        viewModel.loadMedia(mediaId, episodeId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // ExoPlayer View
        media?.let { currentMedia ->
            ExoPlayerView(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Gesture Overlay
        PlayerGestureOverlay(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )

        // Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(300))
        ) {
            PlayerControlsOverlay(
                viewModel = viewModel,
                onBackClick = onBackClick
            )
        }

        // Skip Intro Button
        AnimatedVisibility(
            visible = showSkipIntro,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            SkipButton(
                text = "Skip Intro",
                onClick = { viewModel.skipIntro() },
                modifier = Modifier.padding(16.dp)
            )
        }

        // Skip Outro Button
        AnimatedVisibility(
            visible = showSkipOutro,
            modifier = Modifier.align(Alignment.BottomEnd),
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            SkipButton(
                text = "Skip Outro",
                onClick = { viewModel.skipOutro() },
                modifier = Modifier.padding(16.dp)
            )
        }

        // Next Episode Button
        AnimatedVisibility(
            visible = showNextEpisode,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = slideInHorizontally { it } + fadeIn(),
            exit = slideOutHorizontally { it } + fadeOut()
        ) {
            NextEpisodeButton(
                onClick = { viewModel.playNextEpisode() },
                modifier = Modifier.padding(end = 16.dp)
            )
        }

        // Brightness Indicator
        AnimatedVisibility(
            visible = showBrightnessIndicator,
            modifier = Modifier.align(Alignment.CenterStart),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VolumeBrightnessIndicator(
                icon = Icons.Default.WbSunny,
                value = brightness,
                label = "Brightness"
            )
        }

        // Volume Indicator
        AnimatedVisibility(
            visible = showVolumeIndicator,
            modifier = Modifier.align(Alignment.CenterEnd),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VolumeBrightnessIndicator(
                icon = if (volume > 0) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                value = volume,
                label = "Volume"
            )
        }

        // Seek Indicator
        AnimatedVisibility(
            visible = showSeekIndicator,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SeekIndicator(position = seekPosition)
        }

        // Loading State
        if (playerState.isBuffering) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = PrimaryRed,
                    strokeWidth = 3.dp
                )
            }
        }
    }
}

@Composable
fun ExoPlayerView(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        viewModel.setExoPlayer(exoPlayer)
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
            }
        },
        modifier = modifier
    )
}

@Composable
fun PlayerGestureOverlay(
    viewModel: PlayerViewModel,
    modifier: Modifier = Modifier
) {
    var initialTouchX by remember { mutableFloatStateOf(0f) }
    var initialTouchY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragType by remember { mutableStateOf<DragType?>(null) }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { viewModel.toggleControls() },
                    onDoubleTap = { offset ->
                        val screenWidth = size.width
                        if (offset.x < screenWidth / 2) {
                            viewModel.seekBackward()
                        } else {
                            viewModel.seekForward()
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        initialTouchX = offset.x
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        dragType = null
                        viewModel.hideSeekIndicator()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        viewModel.onHorizontalDrag(dragAmount)
                    }
                )
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialTouchX = offset.x
                        initialTouchY = offset.y
                        isDragging = true
                        val screenWidth = size.width
                        dragType = if (offset.x < screenWidth / 2) {
                            DragType.BRIGHTNESS
                        } else {
                            DragType.VOLUME
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        dragType = null
                        viewModel.hideBrightnessIndicator()
                        viewModel.hideVolumeIndicator()
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        when (dragType) {
                            DragType.BRIGHTNESS -> viewModel.onBrightnessDrag(dragAmount)
                            DragType.VOLUME -> viewModel.onVolumeDrag(dragAmount)
                            else -> {}
                        }
                    }
                )
            }
    )
}

enum class DragType { BRIGHTNESS, VOLUME }

@Composable
fun PlayerControlsOverlay(
    viewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val playerState by viewModel.playerState.collectAsState()
    val media by viewModel.media.collectAsState()
    val isLocked by viewModel.isLocked.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                    Text(
                        text = media?.title ?: "",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    if (media?.contentType != ContentType.MOVIE) {
                        Text(
                            text = "Episode ${viewModel.currentEpisodeNumber}",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(onClick = { viewModel.toggleLock() }) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Lock",
                        tint = TextPrimary
                    )
                }

                IconButton(onClick = { viewModel.enterPipMode() }) {
                    Icon(
                        imageVector = Icons.Default.PictureInPicture,
                        contentDescription = "PiP",
                        tint = TextPrimary
                    )
                }
            }
        }

        if (!isLocked) {
            // Center Play/Pause
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                // Previous (TV/Anime only)
                if (media?.contentType != ContentType.MOVIE) {
                    IconButton(
                        onClick = { viewModel.playPreviousEpisode() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = TextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Play/Pause
                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (playerState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (playerState.isPlaying) "Pause" else "Play",
                        tint = TextPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Next (TV/Anime only)
                if (media?.contentType != ContentType.MOVIE) {
                    IconButton(
                        onClick = { viewModel.playNextEpisode() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = TextPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Bottom Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    // Progress Slider
                    Slider(
                        value = if (playerState.duration > 0) {
                            playerState.currentPosition.toFloat() / playerState.duration.toFloat()
                        } else 0f,
                        onValueChange = { viewModel.onSeek(it) },
                        onValueChangeFinished = { viewModel.onSeekComplete() },
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryRed,
                            activeTrackColor = PrimaryRed,
                            inactiveTrackColor = TextMuted.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Time and Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Current Time / Duration
                        Text(
                            text = "${formatTime(playerState.currentPosition)} / ${formatTime(playerState.duration)}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Quality
                            TextButton(onClick = { viewModel.showQualitySelector() }) {
                                Text(
                                    text = playerState.currentQuality,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }

                            // Speed
                            TextButton(onClick = { viewModel.showSpeedSelector() }) {
                                Text(
                                    text = "${playerState.playbackSpeed}x",
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }

                            // Audio
                            IconButton(onClick = { viewModel.showAudioSelector() }) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = "Audio",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Subtitles
                            IconButton(onClick = { viewModel.showSubtitleSelector() }) {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = "Subtitles",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Fullscreen
                            IconButton(onClick = { viewModel.toggleFullscreen() }) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Fullscreen",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SkipButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Surface.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun NextEpisodeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Surface(
            color = PrimaryRed.copy(alpha = 0.9f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Next Episode",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun VolumeBrightnessIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    label: String
) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { value },
                modifier = Modifier.width(100.dp),
                color = PrimaryRed,
                trackColor = TextMuted.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(value * 100).toInt()}%",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SeekIndicator(position: Long) {
    Surface(
        color = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = formatTime(position),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
    }
}

fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60)) % 60
    val hours = millis / (1000 * 60 * 60)

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
