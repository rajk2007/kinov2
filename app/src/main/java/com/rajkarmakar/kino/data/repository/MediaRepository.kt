package com.rajkarmakar.kino.data.repository

import com.rajkarmakar.kino.data.model.*
import kotlinx.coroutines.delay
import javax.inject.Singleton

@Singleton
class MediaRepository constructor() {

    private val mockMedia = listOf(
        MediaItem(
            id = "1",
            title = "Dune: Part Two",
            overview = "Paul Atreides unites with Chani and the Fremen while seeking revenge against those who destroyed his family.",
            contentType = ContentType.MOVIE,
            posterUrl = "https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/xOMo8BRK7PfcJv9JCnx7s5hjhEF.jpg",
            rating = 8.3,
            year = 2024,
            runtime = 166,
            genres = listOf("Sci-Fi", "Adventure"),
            studios = listOf("Legendary Pictures"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true
        ),
        MediaItem(
            id = "2",
            title = "Jujutsu Kaisen",
            overview = "A boy swallows a cursed talisman - the finger of a demon - and becomes cursed himself.",
            contentType = ContentType.ANIME,
            posterUrl = "https://image.tmdb.org/t/p/w500/fVwBA9YL0r6MPXx4LXL3g7j5R1.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/7SSm2hOJ5Yk8f1j9VZq1l1jK1jL.jpg",
            rating = 8.6,
            year = 2020,
            genres = listOf("Action", "Supernatural"),
            studios = listOf("MAPPA"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true,
            episodes = listOf(
                Episode(id = "e1", episodeNumber = 1, seasonNumber = 1, title = "Ryomen Sukuna", duration = 1440, hasHindiDub = true),
                Episode(id = "e2", episodeNumber = 2, seasonNumber = 1, title = "For Myself", duration = 1440, hasHindiDub = true),
                Episode(id = "e3", episodeNumber = 3, seasonNumber = 1, title = "Girl of Steel", duration = 1440, hasHindiDub = true)
            )
        ),
        MediaItem(
            id = "3",
            title = "Stranger Things",
            overview = "When a young boy disappears, his mother, a police chief and his friends must confront terrifying supernatural forces.",
            contentType = ContentType.TV,
            posterUrl = "https://image.tmdb.org/t/p/w500/49WJfeN0moxb9IPfGn8AIqMGskD.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/56v2KjBlU4XaOv9rVYEQypROD7P.jpg",
            rating = 8.7,
            year = 2016,
            genres = listOf("Sci-Fi", "Horror", "Drama"),
            studios = listOf("Netflix"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true
        ),
        MediaItem(
            id = "4",
            title = "Pushpa 2: The Rule",
            overview = "Pushpa Raj continues his rise in the world of red sandalwood smuggling.",
            contentType = ContentType.MOVIE,
            posterUrl = "https://image.tmdb.org/t/p/w500/1E5baAaEse26fej7uHcjOgEE2t2.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/3V4kLQg0kSqPLctI5ziYWabAZYF.jpg",
            rating = 7.8,
            year = 2024,
            runtime = 181,
            genres = listOf("Action", "Drama"),
            studios = listOf("Mythri Movie Makers"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true
        ),
        MediaItem(
            id = "5",
            title = "Demon Slayer",
            overview = "A family is attacked by demons and only two members survive - Tanjiro and his sister Nezuko.",
            contentType = ContentType.ANIME,
            posterUrl = "https://image.tmdb.org/t/p/w500/xUfRZu2mi8j27Q2GKk6LwX7V1P.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/3GQKYh6Trm8pxYV9X9j1j8R1.jpg",
            rating = 8.7,
            year = 2019,
            genres = listOf("Action", "Fantasy"),
            studios = listOf("ufotable"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true
        ),
        MediaItem(
            id = "6",
            title = "The Batman",
            overview = "When a sadistic serial killer begins murdering key political figures in Gotham, Batman is forced to investigate.",
            contentType = ContentType.MOVIE,
            posterUrl = "https://image.tmdb.org/t/p/w500/74xTEgt7R36FpoooUQH8Sc8G6s.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/5P8SmMzSNYikXpxil6BYZJ1lLl9.jpg",
            rating = 7.8,
            year = 2022,
            runtime = 176,
            genres = listOf("Action", "Crime"),
            studios = listOf("Warner Bros."),
            isHd = true,
            hasHindiDub = false,
            hasSubtitles = true
        ),
        MediaItem(
            id = "7",
            title = "Attack on Titan",
            overview = "After his hometown is destroyed, young Eren Jaeger vows to cleanse the earth of the giant humanoid Titans.",
            contentType = ContentType.ANIME,
            posterUrl = "https://image.tmdb.org/t/p/w500/8ChCpCYxh9YXusmHwcE1WsX7Tts.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/3V4kLQg0kSqPLctI5ziYWabAZYF.jpg",
            rating = 9.1,
            year = 2013,
            genres = listOf("Action", "Drama"),
            studios = listOf("WIT Studio", "MAPPA"),
            isHd = true,
            hasHindiDub = true,
            hasSubtitles = true
        ),
        MediaItem(
            id = "8",
            title = "Breaking Bad",
            overview = "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine.",
            contentType = ContentType.TV,
            posterUrl = "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
            backdropUrl = "https://image.tmdb.org/t/p/original/84XPpjGvxNyExjSuLQe0SzioErt.jpg",
            rating = 9.5,
            year = 2008,
            genres = listOf("Crime", "Drama"),
            studios = listOf("Sony Pictures"),
            isHd = true,
            hasHindiDub = false,
            hasSubtitles = true
        )
    )

    suspend fun getHomeContent(): List<ContentRail> {
        delay(800) // Simulate network
        return listOf(
            ContentRail("trending", "Trending Now", RailType.TRENDING, mockMedia.shuffled()),
            ContentRail("hindi", "Hindi Dubbed For You", RailType.HINDI_DUBBED, mockMedia.filter { it.hasHindiDub }),
            ContentRail("movies", "Popular Movies", RailType.POPULAR_MOVIES, mockMedia.filter { it.contentType == ContentType.MOVIE }),
            ContentRail("anime", "Anime Spotlight", RailType.ANIME_SPOTLIGHT, mockMedia.filter { it.contentType == ContentType.ANIME }),
            ContentRail("series", "Popular Series", RailType.POPULAR_SERIES, mockMedia.filter { it.contentType == ContentType.TV }),
            ContentRail("top", "Top Rated", RailType.TOP_RATED, mockMedia.sortedByDescending { it.rating }),
            ContentRail("weekend", "Weekend Picks", RailType.WEEKEND_PICKS, mockMedia.shuffled()),
            ContentRail("gems", "Hidden Gems", RailType.HIDDEN_GEMS, mockMedia.shuffled().take(4))
        )
    }

    suspend fun search(query: String, type: ContentType?): List<MediaItem> {
        delay(500)
        return mockMedia.filter { media ->
            val matchesQuery = media.title.contains(query, ignoreCase = true) ||
                    media.overview.contains(query, ignoreCase = true)
            val matchesType = type == null || media.contentType == type
            matchesQuery && matchesType
        }
    }

    suspend fun getMediaDetails(mediaId: String): MediaItem {
        delay(400)
        return mockMedia.find { it.id == mediaId } ?: mockMedia.first()
    }

    suspend fun getContinueWatching(): List<MediaItem> = mockMedia.take(3)
    suspend fun getWatchlist(): List<MediaItem> = mockMedia.shuffled().take(4)
    suspend fun getFavorites(): List<MediaItem> = mockMedia.shuffled().take(3)
    suspend fun getDownloads(): List<MediaItem> = emptyList()
    suspend fun getHistory(): List<MediaItem> = mockMedia.shuffled().take(5)
    suspend fun getCompleted(): List<MediaItem> = mockMedia.take(2)
}
