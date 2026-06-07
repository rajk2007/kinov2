package com.rajkarmakar.kino.data.model

import kotlinx.serialization.Serializable

// ============================================
// CORE MEDIA MODELS
// ============================================

enum class ContentType {
    MOVIE, TV, ANIME
}

enum class MediaStatus {
    UPCOMING, RELEASED, AIRING, COMPLETED, CANCELLED
}

@Serializable
data class MediaItem(
    val id: String,
    val title: String,
    val originalTitle: String? = null,
    val overview: String,
    val contentType: ContentType,
    val posterUrl: String? = null,
    val backdropUrl: String? = null,
    val rating: Double = 0.0,
    val voteCount: Int = 0,
    val year: Int? = null,
    val runtime: Int? = null,
    val genres: List<String> = emptyList(),
    val studios: List<String> = emptyList(),
    val status: MediaStatus = MediaStatus.RELEASED,
    val isHd: Boolean = true,
    val hasHindiDub: Boolean = false,
    val hasSubtitles: Boolean = true,
    val isAdult: Boolean = false,
    val seasons: List<Season>? = null,
    val episodes: List<Episode>? = null,
    val watchProgress: WatchProgress? = null,
    val isInWatchlist: Boolean = false,
    val isFavorite: Boolean = false,
    val isDownloaded: Boolean = false,
    val addedToHistoryAt: Long? = null,
    val completedAt: Long? = null
)

@Serializable
data class Season(
    val id: String,
    val seasonNumber: Int,
    val name: String,
    val overview: String? = null,
    val posterUrl: String? = null,
    val episodeCount: Int = 0,
    val airDate: String? = null,
    val episodes: List<Episode> = emptyList()
)

@Serializable
data class Episode(
    val id: String,
    val episodeNumber: Int,
    val seasonNumber: Int,
    val title: String,
    val overview: String? = null,
    val thumbnailUrl: String? = null,
    val duration: Int = 0,
    val airDate: String? = null,
    val watchProgress: WatchProgress? = null,
    val isWatched: Boolean = false,
    val hasHindiDub: Boolean = false,
    val hasSubtitles: Boolean = true,
    val introStart: Long? = null,
    val introEnd: Long? = null,
    val outroStart: Long? = null,
    val outroEnd: Long? = null
)

@Serializable
data class WatchProgress(
    val mediaId: String,
    val episodeId: String? = null,
    val position: Long = 0,
    val duration: Long = 0,
    val lastWatchedAt: Long = System.currentTimeMillis(),
    val percentage: Float = 0f
)

// ============================================
// SEARCH & DISCOVERY
// ============================================

@Serializable
data class SearchResult(
    val query: String,
    val results: List<MediaItem>,
    val totalResults: Int = 0,
    val page: Int = 1
)

@Serializable
data class ContentRail(
    val id: String,
    val title: String,
    val type: RailType,
    val items: List<MediaItem>,
    val isHorizontal: Boolean = true
)

enum class RailType {
    CONTINUE_WATCHING,
    TRENDING,
    HINDI_DUBBED,
    POPULAR_MOVIES,
    POPULAR_SERIES,
    ANIME_SPOTLIGHT,
    LATEST_EPISODES,
    TOP_RATED,
    AWARD_WINNERS,
    HIDDEN_GEMS,
    WEEKEND_PICKS,
    BECAUSE_YOU_WATCHED,
    RECOMMENDED
}

// ============================================
// USER & PREFERENCES
// ============================================

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val avatarUrl: String? = null,
    val email: String? = null,
    val isPremium: Boolean = true,
    val membershipExpiry: Long? = null,
    val preferences: UserPreferences = UserPreferences()
)

@Serializable
data class UserPreferences(
    val preferredLanguage: String = "hi",
    val subtitleLanguage: String = "en",
    val subtitleSize: Float = 1.0f,
    val subtitleColor: String = "#FFFFFF",
    val subtitleBackground: String = "#80000000",
    val playbackSpeed: Float = 1.0f,
    val autoPlayNext: Boolean = true,
    val skipIntro: Boolean = true,
    val skipOutro: Boolean = true,
    val downloadQuality: String = "720p",
    val wifiOnlyDownloads: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val theme: KinoTheme = KinoTheme.AMOLED_BLACK,
    val replayIntro: Boolean = false
)

// ============================================
// PLAYER
// ============================================

@Serializable
data class MediaSource(
    val id: String,
    val url: String,
    val quality: String,
    val language: String,
    val isDefault: Boolean = false,
    val type: SourceType = SourceType.HLS
)

enum class SourceType {
    HLS, DASH, MP4, MKV
}

@Serializable
data class SubtitleTrack(
    val id: String,
    val language: String,
    val label: String,
    val url: String,
    val isDefault: Boolean = false
)

@Serializable
data class AudioTrack(
    val id: String,
    val language: String,
    val label: String,
    val isDefault: Boolean = false
)

@Serializable
data class PlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val bufferedPosition: Long = 0,
    val playbackSpeed: Float = 1.0f,
    val currentQuality: String = "Auto",
    val currentAudioTrack: AudioTrack? = null,
    val currentSubtitleTrack: SubtitleTrack? = null,
    val isControlsVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isPipMode: Boolean = false,
    val brightness: Float = 0.5f,
    val volume: Float = 0.5f,
    val error: String? = null
)

// ============================================
// DOWNLOADS
// ============================================

@Serializable
data class DownloadItem(
    val id: String,
    val mediaItem: MediaItem,
    val episodeId: String? = null,
    val quality: String,
    val filePath: String? = null,
    val progress: Float = 0f,
    val status: DownloadStatus = DownloadStatus.PENDING,
    val totalBytes: Long = 0,
    val downloadedBytes: Long = 0,
    val startedAt: Long? = null,
    val completedAt: Long? = null
)

enum class DownloadStatus {
    PENDING, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED
}

// ============================================
// EXTENSIONS (CloudStream-ready)
// ============================================

interface MediaProvider {
    val id: String
    val name: String
    val iconUrl: String?
    val isEnabled: Boolean

    suspend fun search(query: String, page: Int = 1): SearchResult
    suspend fun getMediaDetails(mediaId: String): MediaItem
    suspend fun getEpisodes(seasonId: String): List<Episode>
    suspend fun getStreamUrl(episodeId: String): List<MediaSource>
    suspend fun getHomeContent(): List<ContentRail>
}

// ============================================
// LIBRARY SECTIONS
// ============================================

enum class LibrarySection {
    CONTINUE_WATCHING,
    WATCHLIST,
    FAVORITES,
    DOWNLOADS,
    HISTORY,
    COMPLETED
}
