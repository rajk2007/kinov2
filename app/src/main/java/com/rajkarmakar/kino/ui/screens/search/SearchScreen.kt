package com.rajkarmakar.kino.ui.screens.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.data.model.ContentType
import com.rajkarmakar.kino.data.model.MediaItem
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.SearchViewModel
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(
    onMediaClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val trendingSearches by viewModel.trendingSearches.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        // Search Bar
        Surface(
            color = SurfaceElevated,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            TextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { 
                    Text(
                        text = "Search movies, shows, anime…",
                        color = TextMuted,
                        fontSize = 16.sp
                    ) 
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = TextMuted
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextMuted
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        viewModel.search()
                    }
                ),
                singleLine = true
            )
        }

        // Filter Tabs
        if (query.isNotEmpty()) {
            SearchFilterTabs(
                selectedTab = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )
        }

        // Content
        when {
            isSearching -> SearchSkeletonLoader()
            query.isEmpty() -> SearchDiscoveryContent(
                trendingSearches = trendingSearches,
                recentSearches = recentSearches,
                onTrendingClick = { viewModel.onQueryChange(it); viewModel.search() },
                onRecentClick = { viewModel.onQueryChange(it); viewModel.search() }
            )
            searchResults.isEmpty() -> EmptySearchState()
            else -> SearchResultsGrid(
                results = searchResults,
                onMediaClick = onMediaClick
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }
}

@Composable
fun SearchFilterTabs(
    selectedTab: ContentType?,
    onTabSelected: (ContentType?) -> Unit
) {
    val tabs = listOf(
        null to "All",
        ContentType.MOVIE to "Movies",
        ContentType.TV to "TV Shows",
        ContentType.ANIME to "Anime"
    )

    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedTab },
        containerColor = Background,
        contentColor = PrimaryRed,
        edgePadding = 16.dp,
        divider = {},
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty()) {
                val selectedIndex = tabs.indexOfFirst { it.first == selectedTab }
                if (selectedIndex >= 0 && selectedIndex < tabPositions.size) {
                    Box(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedIndex])
                            .height(3.dp)
                            .background(PrimaryRed, RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    )
                }
            }
        }
    ) {
        tabs.forEach { (type, label) ->
            Tab(
                selected = selectedTab == type,
                onClick = { onTabSelected(type) },
                text = {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == type) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selectedTab == type) PrimaryRed else TextSecondary
                    )
                }
            )
        }
    }
}

@Composable
fun SearchDiscoveryContent(
    trendingSearches: List<String>,
    recentSearches: List<String>,
    onTrendingClick: (String) -> Unit,
    onRecentClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Trending Searches
        if (trendingSearches.isNotEmpty()) {
            item {
                SearchSectionTitle("Trending Searches")
            }
            items(trendingSearches) { query ->
                TrendingSearchItem(
                    query = query,
                    rank = trendingSearches.indexOf(query) + 1,
                    onClick = { onTrendingClick(query) }
                )
            }
        }

        // Recent Searches
        if (recentSearches.isNotEmpty()) {
            item {
                SearchSectionTitle("Recent Searches")
            }
            items(recentSearches) { query ->
                RecentSearchItem(
                    query = query,
                    onClick = { onRecentClick(query) }
                )
            }
        }
    }
}

@Composable
fun SearchSectionTitle(title: String) {
    Text(
        text = title,
        style = KinoTypography.HeadlineLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun TrendingSearchItem(
    query: String,
    rank: Int,
    onClick: () -> Unit
) {
    val rankColor = when (rank) {
        1 -> PrimaryRed
        2 -> Color(0xFFFFA726)
        3 -> Color(0xFF66BB6A)
        else -> TextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rank.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = rankColor,
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = query,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun RecentSearchItem(
    query: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = query,
            fontSize = 16.sp,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SearchResultsGrid(
    results: List<MediaItem>,
    onMediaClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(results, key = { it.id }) { media ->
            SearchResultCard(
                media = media,
                onClick = { onMediaClick(media.id) }
            )
        }
    }
}

@Composable
fun SearchResultCard(
    media: MediaItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Poster
        AsyncImage(
            model = media.posterUrl,
            contentDescription = media.title,
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder_poster),
            error = painterResource(R.drawable.placeholder_poster)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = media.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                media.year?.let {
                    Text(
                        text = it.toString(),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                if (media.rating > 0) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFA726),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "%.1f".format(media.rating),
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Badges
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (media.hasHindiDub) {
                    SearchBadge("Hindi", PrimaryRed)
                }
                if (media.isHd) {
                    SearchBadge("HD", SurfaceElevated)
                }
                if (media.hasSubtitles) {
                    SearchBadge("CC", SecondaryPurple)
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

@Composable
fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No results found",
                style = KinoTypography.HeadlineMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun SearchSkeletonLoader() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        repeat(8) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(120.dp)
                        .background(SurfaceElevated, RoundedCornerShape(12.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .width(150.dp)
                            .height(16.dp)
                            .background(SurfaceElevated, RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .background(SurfaceElevated, RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}
