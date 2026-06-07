package com.rajkarmakar.kino.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.data.model.KinoTheme
import com.rajkarmakar.kino.data.model.UserProfile
import com.rajkarmakar.kino.ui.theme.*
import com.rajkarmakar.kino.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onReplayIntro: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val selectedTheme by viewModel.selectedTheme.collectAsState()
    val showThemeDialog by viewModel.showThemeDialog.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        ProfileHeader(user = user)

        Spacer(modifier = Modifier.height(24.dp))

        // Settings Sections
        SettingsSection(title = "Preferences") {
            SettingsItem(
                icon = Icons.Default.Language,
                title = "Language Preferences",
                subtitle = "Hindi, English",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Subtitles,
                title = "Subtitle Preferences",
                subtitle = "English, Size: Normal",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Speed,
                title = "Playback Settings",
                subtitle = "Auto-play next, Skip intro",
                onClick = { }
            )
        }

        SettingsSection(title = "Appearance") {
            SettingsItem(
                icon = Icons.Default.Palette,
                title = "Theme Selection",
                subtitle = selectedTheme.name.replace("_", " ").lowercase()
                    .replaceFirstChar { it.uppercase() },
                onClick = { viewModel.showThemeSelector() }
            )
        }

        SettingsSection(title = "General") {
            SettingsItem(
                icon = Icons.Default.Download,
                title = "Downloads Manager",
                subtitle = "Manage offline content",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notification Settings",
                subtitle = "Enabled",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Refresh,
                title = "Replay Intro Animation",
                subtitle = "Watch the KINO intro again",
                onClick = onReplayIntro
            )
        }

        SettingsSection(title = "About") {
            SettingsItem(
                icon = Icons.Default.Info,
                title = "About KINO",
                subtitle = "Version 1.0.0 • by Raj Karmakar",
                onClick = { }
            )
            SettingsItem(
                icon = Icons.Default.Extension,
                title = "Extensions Manager",
                subtitle = "CloudStream plugins (Coming Soon)",
                onClick = { }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Brand Footer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Text(
                text = "KINO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary.copy(alpha = 0.5f),
                letterSpacing = 4.sp
            )
            Text(
                text = "by Raj Karmakar",
                fontSize = 12.sp,
                color = TextMuted.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )
        }
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        ThemeSelectionDialog(
            selectedTheme = selectedTheme,
            onThemeSelected = { viewModel.onThemeSelected(it) },
            onDismiss = { viewModel.hideThemeSelector() }
        )
    }
}

@Composable
fun ProfileHeader(user: UserProfile?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        PrimaryRed.copy(alpha = 0.15f),
                        Background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SurfaceElevated)
                    .border(2.dp, PrimaryRed.copy(alpha = 0.5f), CircleShape)
            ) {
                if (user?.avatarUrl != null) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            Text(
                text = user?.name ?: "Guest",
                style = KinoTypography.HeadlineLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Membership Status
            Surface(
                color = PrimaryRed.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = user?.membershipStatus ?: "Premium Member",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryRed,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Surface(
            color = Surface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceElevated, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    selectedTheme: KinoTheme,
    onThemeSelected: (KinoTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val themes = listOf(
        KinoTheme.AMOLED_BLACK to "AMOLED Black" to Color(0xFF050505),
        KinoTheme.CINEMATIC_RED to "Cinematic Red" to Color(0xFF0A0000),
        KinoTheme.PURPLE_GLOW to "Purple Glow" to Color(0xFF0A0510),
        KinoTheme.MIDNIGHT_BLUE to "Midnight Blue" to Color(0xFF020510),
        KinoTheme.GOLDEN_PRESTIGE to "Golden Prestige" to Color(0xFF0A0800)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = "Select Theme",
                style = KinoTypography.HeadlineLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                themes.forEach { (themePair, previewColor) ->
                    val (theme, name) = themePair
                    val isSelected = selectedTheme == theme

                    Surface(
                        color = if (isSelected) PrimaryRed.copy(alpha = 0.2f) else SurfaceElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) BorderStroke(1.dp, PrimaryRed) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onThemeSelected(theme) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(previewColor)
                                    .border(1.dp, TextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Text(
                                text = name,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) PrimaryRed else TextPrimary
                            )

                            if (isSelected) {
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = PrimaryRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = PrimaryRed)
            }
        }
    )
}
