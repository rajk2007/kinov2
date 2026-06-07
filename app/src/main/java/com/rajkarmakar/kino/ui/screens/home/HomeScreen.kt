package com.rajkarmakar.kino.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.data.model.ContentRail
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.HomeUiState
import com.rajkarmakar.kino.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun HomeScreen(
    onMediaClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val heroItems by viewModel.heroItems.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when (val state = uiState) {
            is HomeUiState.Loading -> HomeSkeletonLoader()
            is HomeUiState.Success -> {
                HomeContent(
                    rails = (state as HomeUiState.Success).rails,
                    heroItems = heroItems,
                    onMediaClick = onMediaClick
                )
            }
            is HomeUiState.Error -> {
                ErrorState(
                    message = (state as HomeUiState.Error).message,
                    onRetry = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    rails: List<ContentRail>,
    heroItems: List<MediaItem>,
    onMediaClick: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            HeroCarousel(
                items = heroItems,
                onMediaClick = onMediaClick
            )
        }

        rails.forEach { rail ->
            item(key = rail.id) {
                ContentRailSection(
                    rail = rail,
                    onMediaClick = onMediaClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroCarousel(
    items: List<MediaItem>,
    onMediaClick: (String) -> Unit
) {
    if (items.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(6000)
            val nextPage = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(modifier = Modifier.fillMaxWidth().height(520.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            ).absoluteValue

            HeroItem(
                media = items[page],
                pageOffset = pageOffset,
                onMediaClick = onMediaClick
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEachIndexed { index, _ ->
                val isSelected = pagerState.currentPage == index
                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 6.dp,
                    animationSpec = tween(300),
                    label = "indicator"
                )

                Box(
                    modifier = Modifier
                        .width(width)
                        .height(6.dp)
                        .background(
                            if (isSelected) PrimaryRed else TextMuted.copy(alpha = 0.4f),
                            RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

@Composable
fun HeroItem(
    media: MediaItem,
    pageOffset: Float,
    onMediaClick: (String) -> Unit
) {
    val scale = 1f + (pageOffset * 0.15f)
    val alpha = 1f - (pageOffset * 0.5f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable { onMediaClick(media.id) }
    ) {
        AsyncImage(
            model = media.backdropUrl ?: media.posterUrl,
            contentDescription = media.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_backdrop),
            error = painterResource(R.drawable.placeholder_backdrop)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(HeroGradient)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryRed.copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.3f),
                        radius = 0.8f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(horizontal = 24.dp, vertical = 100.dp)
        ) {
            AnimatedVisibility(
                visible = media.hasHindiDub,
                enter = fadeIn() + slideInVertically { it }
            ) {
                Surface(
                    color = PrimaryRed.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "HINDI DUB",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        letterSpacing = 1.sp
                    )
                }
            }

            Text(
                text = media.title,
                style = KinoTypography.DisplayMedium.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(0f, 8f),
                        blurRadius = 24f
                    )
                ),
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
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "%.1f".format(media.rating),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }

                media.year?.let {
                    Text(
                        text = it.toString(),
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                if (media.isHd) {
                    Surface(
                        color = SurfaceElevated,
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, TextMuted)
                    ) {
                        Text(
                            text = "HD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                media.genres.take(2).forEach { genre ->
                    Text(
                        text = genre,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onMediaClick(media.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(44.dp)
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
                    modifier = Modifier.height(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Watchlist",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ContentRailSection(
    rail: ContentRail,
    onMediaClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = rail.title,
            style = KinoTypography.HeadlineLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = rail.items,
                key = { it.id }
            ) { media ->
                MediaCard(
                    media = media,
                    onClick = { onMediaClick(media.id) },
                    onLongClick = { }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaCard(
    media: MediaItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 16.dp else 4.dp,
        label = "elevation"
    )

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(210.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(elevation, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        AsyncImage(
            model = media.posterUrl,
            contentDescription = media.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CardGradient)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopStart),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (media.isHd) {
                Badge(text = "HD", color = SurfaceElevated)
            }
            if (media.hasHindiDub) {
                Badge(text = "HINDI", color = PrimaryRed)
            }
            if (media.hasSubtitles) {
                Badge(text = "CC", color = SecondaryPurple)
            }
        }

        if (media.rating > 0) {
            Surface(
                color = Color(0xFFFFA726).copy(alpha = 0.9f),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = "%.1f".format(media.rating),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }

        media.watchProgress?.let { progress ->
            if (progress.percentage > 0 && progress.percentage < 0.95f) {
                LinearProgressIndicator(
                    progress = { progress.percentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .align(Alignment.BottomCenter),
                    color = PrimaryRed,
                    trackColor = SurfaceElevated
                )
            }
        }

        Text(
            text = media.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun Badge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.9f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun HomeSkeletonLoader() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .background(Surface)
                .shimmerEffect()
        )

        repeat(4) {
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(24.dp)
                        .padding(horizontal = 16.dp)
                        .background(SurfaceElevated, RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )

                LazyRow(
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        Box(
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .background(SurfaceElevated, RoundedCornerShape(16.dp))
                                .shimmerEffect()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Something went wrong",
            style = KinoTypography.HeadlineLarge,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = KinoTypography.BodyMedium,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed)
        ) {
            Text("Retry")
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                SurfaceElevated,
                SurfaceElevated.copy(alpha = 0.5f),
                SurfaceElevated
            ),
            start = Offset(translateAnimation - 500, 0f),
            end = Offset(translateAnimation + 500, 0f)
        )
    )
}
