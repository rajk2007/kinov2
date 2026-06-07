package com.rajkarmakar.kino.ui.screens.library

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.data.model.LibrarySection
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.LibraryViewModel

@Composable
fun LibraryScreen(
    onMediaClick: (String) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val selectedSection by viewModel.selectedSection.collectAsState()
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        // Header
        Text(
            text = "Library",
            style = KinoTypography.DisplaySmall,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
        )

        // Section Tabs
        LibrarySectionTabs(
            selectedSection = selectedSection,
            onSectionSelected = { viewModel.onSectionSelected(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        when {
            isLoading -> LibrarySkeletonLoader()
            items.isEmpty() -> EmptyLibraryState(selectedSection)
            else -> LibraryGrid(
                items = items,
                onMediaClick = onMediaClick
            )
        }
    }
}

@Composable
fun LibrarySectionTabs(
    selectedSection: LibrarySection,
    onSectionSelected: (LibrarySection) -> Unit
) {
    val sections = listOf(
        LibrarySection.CONTINUE_WATCHING to "Continue",
        LibrarySection.WATCHLIST to "Watchlist",
        LibrarySection.FAVORITES to "Favorites",
        LibrarySection.DOWNLOADS to "Downloads",
        LibrarySection.HISTORY to "History",
        LibrarySection.COMPLETED to "Completed"
    )

    ScrollableTabRow(
        selectedTabIndex = sections.indexOfFirst { it.first == selectedSection },
        containerColor = Background,
        contentColor = PrimaryRed,
        edgePadding = 16.dp,
        divider = {}
    ) {
        sections.forEach { (section, label) ->
            Tab(
                selected = selectedSection == section,
                onClick = { onSectionSelected(section) },
                text = {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (selectedSection == section) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedSection == section) PrimaryRed else TextSecondary
                    )
                }
            )
        }
    }
}

@Composable
fun LibraryGrid(
    items: List<MediaItem>,
    onMediaClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items, key = { it.id }) { media ->
            LibraryCard(
                media = media,
                onClick = { onMediaClick(media.id) }
            )
        }
    }
}

@Composable
fun LibraryCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            AsyncImage(
                model = media.posterUrl,
                contentDescription = media.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder_poster),
                error = painterResource(R.drawable.placeholder_poster)
            )

            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f))
                        )
                    )
            )

            // Progress indicator
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

            // Watched indicator
            if (media.watchProgress?.percentage?.let { it >= 0.95f } == true) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(PrimaryRed, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = media.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun EmptyLibraryState(section: LibrarySection) {
    val (icon, title, subtitle) = when (section) {
        LibrarySection.CONTINUE_WATCHING -> Triple(
            Icons.Default.PlayCircleOutline,
            "Nothing to continue",
            "Start watching and your progress will appear here"
        )
        LibrarySection.WATCHLIST -> Triple(
            Icons.Default.BookmarkBorder,
            "Your watchlist is empty",
            "Add movies and shows to watch later"
        )
        LibrarySection.FAVORITES -> Triple(
            Icons.Default.FavoriteBorder,
            "No favorites yet",
            "Mark content as favorite to see it here"
        )
        LibrarySection.DOWNLOADS -> Triple(
            Icons.Default.Download,
            "No downloads",
            "Download content to watch offline"
        )
        LibrarySection.HISTORY -> Triple(
            Icons.Default.History,
            "No history",
            "Your watch history will appear here"
        )
        LibrarySection.COMPLETED -> Triple(
            Icons.Default.CheckCircleOutline,
            "Nothing completed",
            "Finished content will appear here"
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = KinoTypography.HeadlineMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = KinoTypography.BodyMedium,
                color = TextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun LibrarySkeletonLoader() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(9) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(SurfaceElevated, RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(12.dp)
                        .background(SurfaceElevated, RoundedCornerShape(4.dp))
                        .shimmerEffect()
                )
            }
        }
    }
}
