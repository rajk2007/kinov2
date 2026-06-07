# KINO — Premium Native Android Streaming Platform

**by Raj Karmakar**

A production-quality native Android streaming application built with Kotlin and Jetpack Compose. KINO delivers a premium entertainment experience focused on Movies, TV Shows, Anime, and Hindi Dubbed Content.

---

## 🎯 Philosophy

This is **NOT** a prototype. This is **NOT** a demo. This is **NOT** a landing page. This is **NOT** a Netflix clone.

KINO is designed as a flagship Android application capable of competing with Netflix, Apple TV, Prime Video, Crunchyroll, Disney+, and JioHotstar.

---

## 🏗️ Architecture

```
KINO/
├── app/
│   ├── src/main/java/com/rajkarmakar/kino/
│   │   ├── data/
│   │   │   ├── local/          # DataStore Preferences
│   │   │   ├── model/          # Data Models & Enums
│   │   │   ├── remote/         # API Interfaces (CloudStream-ready)
│   │   │   └── repository/     # MediaRepository
│   │   ├── di/
│   │   │   └── AppModule.kt    # Hilt DI Module
│   │   ├── service/
│   │   │   └── MediaPlaybackService.kt  # Media3 Foreground Service
│   │   ├── ui/
│   │   │   ├── navigation/     # Navigation Compose
│   │   │   ├── screens/        # All Screens
│   │   │   │   ├── intro/      # Cinematic Intro Animation
│   │   │   │   ├── home/       # Hero Carousel + Content Rails
│   │   │   │   ├── search/     # Premium Search + Discovery
│   │   │   │   ├── details/    # Cinematic Details + Episodes
│   │   │   │   ├── library/    # Personal Cinema Shelf
│   │   │   │   ├── profile/    # Account Hub + Themes
│   │   │   │   └── player/     # Media3 Cinematic Player
│   │   │   ├── theme/          # Complete Design System
│   │   │   └── components/     # Reusable Components
│   │   ├── util/
│   │   ├── viewmodel/          # All ViewModels (MVVM)
│   │   ├── KinoApplication.kt
│   │   └── MainActivity.kt
│   └── src/main/res/           # Resources
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/libs.versions.toml
```

---

## 🛠️ Technology Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Design | Material 3 |
| Navigation | Navigation Compose |
| Architecture | MVVM |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow/StateFlow |
| Media | Media3 ExoPlayer |
| Images | Coil |
| Storage | DataStore Preferences |
| Networking | Retrofit + OkHttp |
| Serialization | Kotlinx Serialization |

---

## ✨ Key Features

### Cinematic Intro Animation
- 3-5 second signature animation
- Glowing red light streak → particles → KINO logo formation
- Purple glow emergence, "O" transforms into play symbol
- "by Raj Karmakar" fade-in
- Blur dissolve transition to Home
- Replay option in Settings

### Premium Home Screen
- Auto-rotating hero carousel with parallax depth
- Dynamic backdrop with cinematic zoom effects
- Content rails: Continue Watching, Trending, Hindi Dubbed, Popular Movies, Anime Spotlight, Top Rated, etc.
- Smart skeleton loaders with shimmer effect
- Glassmorphism and ambient glow

### Advanced Player
- Media3 ExoPlayer architecture
- Full gesture system:
  - Left swipe: Brightness
  - Right swipe: Volume
  - Horizontal swipe: Seek
  - Double tap left: Rewind
  - Double tap right: Fast Forward
- Skip Intro/Outro (TV/Anime only)
- Next Episode button
- PiP support placeholder
- Lock controls
- Quality, Audio, Subtitle, Speed selectors

### Search & Discovery
- Real-time debounced search
- Filter tabs: All, Movies, TV Shows, Anime
- Trending searches, Recent searches
- Metadata-first results with badges

### Library
- Continue Watching, Watchlist, Favorites, Downloads, History, Completed
- Grid layout with progress indicators
- Premium shelf aesthetic

### Profile & Settings
- 5 curated premium themes: AMOLED Black, Cinematic Red, Purple Glow, Midnight Blue, Golden Prestige
- Language preferences, Subtitle preferences, Playback settings
- Downloads Manager, Extensions Manager placeholder
- Replay Intro Animation option

### 18+ Content Warning
- Premium modal with Continue/Go Back actions
- Elegant and non-intrusive

---

## 🎨 Design System

### Colors
- Background: `#050505`
- Surface: `#0B0B0B`
- Primary: `#E50914`
- Secondary: `#7B2FBE`
- Accent: `#FF3D81`
- Text: `#FFFFFF`
- Muted: `#A0A0A0`

### Visual Style
- AMOLED dark theme
- Cinematic gradients
- Dynamic backdrops
- Glassmorphism
- Dynamic blur
- Premium depth
- Layered shadows
- Rounded 2XL corners
- Elegant typography
- Soft red highlights
- Purple glow accents

---

## 🔮 Future CloudStream Integration

This project is **UI-FIRST**. CloudStream integration will be added later with minimal refactoring.

Architecture designed for:
- CloudStream Plugins via `MediaProvider` interface
- TMDb Metadata
- Supabase backend
- Recommendation Systems

The UI remains unchanged regardless of backend source.

---

## 🚀 Getting Started

1. Clone the repository
2. Open in Android Studio (Arctic Fox or later)
3. Sync Gradle
4. Run on device or emulator (API 26+)

---

## 📄 License

Copyright © 2024 Raj Karmakar. All rights reserved.

---

**KINO** — Cinema, reimagined.
