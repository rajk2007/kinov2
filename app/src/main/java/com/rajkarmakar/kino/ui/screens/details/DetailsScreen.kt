package com.rajkarmakar.kino.ui.screens.details

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.data.model.ContentType
import com.rajkarmakar.kino.data.model.Episode
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.DetailsViewModel

@Composable
fun DetailsScreen(
    mediaId: String,
    onBackClick: () -> Unit,
    onWatchClick: (String, String?) -> Unit,
    viewModel: DetailsViewModel = hiltViewModel()
) {
    val media by viewModel.media.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showAdultWarning by viewModel.showAdultWarning.collectAsState()

    LaunchedEffect(mediaId) {
        viewModel.loadMedia(mediaId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when {
            isLoading -> DetailsSkeletonLoader()
            media != null -> {
                DetailsContent(
                    media = media!!,
                    onBackClick = onBackClick,
                    onWatchClick = onWatchClick,
                    showAdultWarning = showAdultWarning,
                    onDismissAdultWarning = { viewModel.dismissAdultWarning() }
                )
            }
        }
    }
}

@Composable
fun DetailsContent(
    media: MediaItem,
    onBackClick: () -> Unit,
    onWatchClick: (String, String?) -> Unit,
    showAdultWarning: Boolean,
    onDismissAdultWarning: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
            // Hero Backdrop
            Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                AsyncImage(
                    model = media.backdropUrl ?: media.posterUrl,
                    contentDescription = media.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder_backdrop),
                    error = painterResource(R.drawable.placeholder_backdrop)
                )

                // Gradient overlays
                Box(modifier = Modifier.fillMaxSize().background(HeroGradient))

                // Top gradient for status bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Background.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }

            // Content
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Floating Poster Card
                Row(
                    modifier = Modifier.offset(y = (-60).dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AsyncImage(
                        model = media.posterUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .shadow(16.dp, RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.placeholder_poster)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(
                            text = media.title,
                            style = KinoTypography.HeadlineLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = Color(0xFFFFA726).copy(alpha = 0.9f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = TextPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "%.1f".format(media.rating),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }

                            media.year?.let {
                                Text(text = it.toString(), fontSize = 14.sp, color = TextSecondary)
                            }

                            if (media.isHd) {
                                Surface(
                                    color = SurfaceElevated,
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(1.dp, TextMuted)
                                ) {
                                    Text(
                                        text = "HD",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Genres
                        Text(
                            text = media.genres.joinToString(" • "),
                            fontSize = 13.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onWatchClick(media.id, null) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Watch Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedButton(
                        onClick = { },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Surface.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.3f)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Surface.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.3f)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = { },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Surface.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.3f)),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Overview
                Text(
                    text = media.overview,
                    style = KinoTypography.BodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Metadata
                DetailsMetadata(media = media)

                Spacer(modifier = Modifier.height(24.dp))

                // Episodes (if TV/Anime)
                if (media.contentType != ContentType.MOVIE && media.episodes != null) {
                    EpisodesSection(
                        episodes = media.episodes,
                        onEpisodeClick = { episodeId ->
                            onWatchClick(media.id, episodeId)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // More Like This
                Text(
                    text = "More Like This",
                    style = KinoTypography.HeadlineLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Placeholder for similar content
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(6) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .background(SurfaceElevated, RoundedCornerShape(16.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Back Button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .size(40.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextPrimary
            )
        }

        // Adult Content Warning
        if (showAdultWarning) {
            AdultContentWarning(
                onContinue = { onDismissAdultWarning() },
                onGoBack = onBackClick
            )
        }
    }
}

@Composable
fun DetailsMetadata(media: MediaItem) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MetadataRow("Runtime", "${media.runtime ?: "—"} min")
        MetadataRow("Genres", media.genres.joinToString(", "))
        MetadataRow("Studios", media.studios.joinToString(", "))
        if (media.hasHindiDub) {
            MetadataRow("Audio", "Hindi, English, Original")
        }
        if (media.hasSubtitles) {
            MetadataRow("Subtitles", "English, Hindi")
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextMuted,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EpisodesSection(
    episodes: List<Episode>,
    onEpisodeClick: (String) -> Unit
) {
    Text(
        text = "Episodes",
        style = KinoTypography.HeadlineLarge,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        episodes.forEach { episode ->
            EpisodeCard(
                episode = episode,
                onClick = { onEpisodeClick(episode.id) }
            )
        }
    }
}

@Composable
fun EpisodeCard(
    episode: Episode,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail
        Box(modifier = Modifier.size(120.dp, 68.dp)) {
            AsyncImage(
                model = episode.thumbnailUrl,
                contentDescription = episode.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_episode)
            )

            // Play overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Progress
            episode.watchProgress?.let { progress ->
                if (progress.percentage > 0 && progress.percentage < 0.95f) {
                    LinearProgressIndicator(
                        progress = { progress.percentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .align(Alignment.BottomCenter),
                        color = PrimaryRed,
                        trackColor = Color.Transparent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${episode.episodeNumber}.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.width(28.dp)
                )
                Text(
                    text = episode.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (episode.hasHindiDub) {
                    SearchBadge("Hindi", PrimaryRed)
                }
                if (episode.isWatched) {
                    Text(
                        text = "Watched",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun AdultContentWarning(
    onContinue: () -> Unit,
    onGoBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Surface,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = PrimaryRed,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "This content may be 18+",
                    style = KinoTypography.HeadlineLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "This title contains mature content that may not be suitable for all audiences.",
                    style = KinoTypography.BodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continue Watching",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = onGoBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Go Back",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DetailsSkeletonLoader() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .background(Surface)
                .shimmerEffect()
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.offset(y = (-60).dp)) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .background(SurfaceElevated, RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.padding(top = 60.dp)) {
                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .height(24.dp)
                            .background(SurfaceElevated, RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(16.dp)
                            .background(SurfaceElevated, RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
