package com.rajkarmakar.kino.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

// ============================================
// KINO PREMIUM COLOR SYSTEM
// ============================================

// Core Palette
val Background = Color(0xFF050505)
val Surface = Color(0xFF0B0B0B)
val SurfaceElevated = Color(0xFF141414)
val SurfaceOverlay = Color(0x1AFFFFFF)
val PrimaryRed = Color(0xFFE50914)
val PrimaryRedGlow = Color(0x40E50914)
val SecondaryPurple = Color(0xFF7B2FBE)
val SecondaryPurpleGlow = Color(0x407B2FBE)
val AccentPink = Color(0xFFFF3D81)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA0A0A0)
val TextMuted = Color(0xFF6B6B6B)
val Success = Color(0xFF4CAF50)
val Warning = Color(0xFFFFA726)
val Error = Color(0xFFE53935)

// Cinematic Gradients
val HeroGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0x80050505),
        Color(0xFF050505)
    ),
    startY = 0f,
    endY = 1000f
)

val CardGradient = Brush.verticalGradient(
    colors = listOf(
        Color.Transparent,
        Color(0xCC050505)
    )
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(
        PrimaryRedGlow,
        Color.Transparent
    )
)

val PurpleGlowGradient = Brush.radialGradient(
    colors = listOf(
        SecondaryPurpleGlow,
        Color.Transparent
    )
)

// ============================================
// THEME VARIANTS
// ============================================

enum class KinoTheme {
    AMOLED_BLACK,
    CINEMATIC_RED,
    PURPLE_GLOW,
    MIDNIGHT_BLUE,
    GOLDEN_PRESTIGE
}

object KinoColors {
    val AmoledBlack = darkColorScheme(
        primary = PrimaryRed,
        onPrimary = TextPrimary,
        secondary = SecondaryPurple,
        onSecondary = TextPrimary,
        background = Background,
        onBackground = TextPrimary,
        surface = Surface,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceElevated,
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextPrimary,
        outline = TextMuted
    )

    val CinematicRed = darkColorScheme(
        primary = Color(0xFFFF1744),
        onPrimary = TextPrimary,
        secondary = Color(0xFFFF5252),
        onSecondary = TextPrimary,
        background = Color(0xFF0A0000),
        onBackground = TextPrimary,
        surface = Color(0xFF140505),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF1A0808),
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextPrimary,
        outline = Color(0xFF4A2020)
    )

    val PurpleGlow = darkColorScheme(
        primary = Color(0xFF9D4EDD),
        onPrimary = TextPrimary,
        secondary = Color(0xFFC77DFF),
        onSecondary = TextPrimary,
        background = Color(0xFF0A0510),
        onBackground = TextPrimary,
        surface = Color(0xFF120B1A),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF1A1025),
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextPrimary,
        outline = Color(0xFF4A3050)
    )

    val MidnightBlue = darkColorScheme(
        primary = Color(0xFF64B5F6),
        onPrimary = TextPrimary,
        secondary = Color(0xFF90CAF9),
        onSecondary = TextPrimary,
        background = Color(0xFF020510),
        onBackground = TextPrimary,
        surface = Color(0xFF080E1A),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF0D1520),
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextPrimary,
        outline = Color(0xFF203050)
    )

    val GoldenPrestige = darkColorScheme(
        primary = Color(0xFFFFD700),
        onPrimary = Color(0xFF050505),
        secondary = Color(0xFFFFE082),
        onSecondary = Color(0xFF050505),
        background = Color(0xFF0A0800),
        onBackground = TextPrimary,
        surface = Color(0xFF141000),
        onSurface = TextPrimary,
        surfaceVariant = Color(0xFF1A1608),
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextPrimary,
        outline = Color(0xFF504020)
    )
}

// ============================================
// TYPOGRAPHY
// ============================================

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object KinoTypography {
    val DisplayLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        lineHeight = 56.sp,
        color = TextPrimary
    )

    val DisplayMedium = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp,
        lineHeight = 44.sp,
        color = TextPrimary
    )

    val DisplaySmall = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.2).sp,
        lineHeight = 36.sp,
        color = TextPrimary
    )

    val HeadlineLarge = TextStyle(
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp,
        lineHeight = 32.sp,
        color = TextPrimary
    )

    val HeadlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.sp,
        lineHeight = 28.sp,
        color = TextPrimary
    )

    val TitleLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
        lineHeight = 26.sp,
        color = TextPrimary
    )

    val TitleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.1.sp,
        lineHeight = 24.sp,
        color = TextPrimary
    )

    val BodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp,
        lineHeight = 24.sp,
        color = TextSecondary
    )

    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.2.sp,
        lineHeight = 20.sp,
        color = TextSecondary
    )

    val LabelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        lineHeight = 20.sp,
        color = TextPrimary
    )

    val LabelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp,
        lineHeight = 16.sp,
        color = TextMuted
    )

    val Caption = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.3.sp,
        lineHeight = 14.sp,
        color = TextMuted
    )
}

// ============================================
// SHAPE SYSTEM
// ============================================

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val KinoShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// ============================================
// COMPOSITION LOCALS
// ============================================

val LocalKinoTheme = staticCompositionLocalOf { KinoTheme.AMOLED_BLACK }

// ============================================
// MAIN THEME COMPOSABLE
// ============================================

import androidx.compose.ui.unit.dp

@Composable
fun KinoTheme(
    theme: KinoTheme = KinoTheme.AMOLED_BLACK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        KinoTheme.AMOLED_BLACK -> KinoColors.AmoledBlack
        KinoTheme.CINEMATIC_RED -> KinoColors.CinematicRed
        KinoTheme.PURPLE_GLOW -> KinoColors.PurpleGlow
        KinoTheme.MIDNIGHT_BLUE -> KinoColors.MidnightBlue
        KinoTheme.GOLDEN_PRESTIGE -> KinoColors.GoldenPrestige
    }

    CompositionLocalProvider(LocalKinoTheme provides theme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = androidx.compose.material3.Typography(
                displayLarge = KinoTypography.DisplayLarge,
                displayMedium = KinoTypography.DisplayMedium,
                displaySmall = KinoTypography.DisplaySmall,
                headlineLarge = KinoTypography.HeadlineLarge,
                headlineMedium = KinoTypography.HeadlineMedium,
                titleLarge = KinoTypography.TitleLarge,
                titleMedium = KinoTypography.TitleMedium,
                bodyLarge = KinoTypography.BodyLarge,
                bodyMedium = KinoTypography.BodyMedium,
                labelLarge = KinoTypography.LabelLarge,
                labelMedium = KinoTypography.LabelMedium
            ),
            shapes = KinoShapes,
            content = content
        )
    }
}
