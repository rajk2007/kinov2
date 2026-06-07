package com.rajkarmakar.kino.ui.screens.intro

import android.view.animation.AnticipateOvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rajkarmakar.kino.ui.theme.Background
import com.rajkarmakar.kino.ui.theme.PrimaryRed
import com.rajkarmakar.kino.ui.theme.SecondaryPurple
import com.rajkarmakar.kino.ui.theme.TextMuted
import com.rajkarmakar.kino.ui.theme.TextPrimary
import com.rajkarmakar.kino.viewmodel.IntroViewModel
import kotlinx.coroutines.delay

@Composable
fun IntroScreen(
    onIntroComplete: () -> Unit,
    viewModel: IntroViewModel = hiltViewModel()
) {
    val shouldShowIntro by viewModel.shouldShowIntro.collectAsState()

    LaunchedEffect(shouldShowIntro) {
        if (!shouldShowIntro) {
            onIntroComplete()
        }
    }

    if (shouldShowIntro) {
        KinoIntroAnimation(
            onAnimationComplete = {
                viewModel.markIntroShown()
                onIntroComplete()
            }
        )
    }
}

@Composable
fun KinoIntroAnimation(
    onAnimationComplete: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")

    // Animation states
    val streakAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "streak"
    )

    val streakWidth by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200, easing = EaseOutExpo),
        label = "streakWidth"
    )

    val logoScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1500, delayMillis = 800, easing = EaseOutElastic),
        label = "logoScale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, delayMillis = 600),
        label = "logoAlpha"
    )

    val glowIntensity by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000, delayMillis = 1000, easing = EaseInOutSine),
        label = "glow"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800, delayMillis = 2000),
        label = "subtitle"
    )

    val pulseScale by animateFloatAsState(
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val blurAmount by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(1500, delayMillis = 3500),
        label = "blur"
    )

    val exitAlpha by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(800, delayMillis = 3800),
        label = "exit"
    )

    // Particle animation
    val particleOffsets = List(30) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000 + (index * 200),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle$index"
        )
    }

    LaunchedEffect(Unit) {
        delay(4500)
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .alpha(exitAlpha)
            .blur(blurAmount.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryRed.copy(alpha = 0.08f * glowIntensity),
                            SecondaryPurple.copy(alpha = 0.04f * glowIntensity),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 0.6f
                    )
                )
        )

        // Particles
        particleOffsets.forEachIndexed { index, offset ->
            val angle = offset.value * Math.PI / 180
            val radius = 80 + (index * 15)
            val x = (kotlin.math.cos(angle) * radius).toFloat()
            val y = (kotlin.math.sin(angle) * radius * 0.6).toFloat()

            Box(
                modifier = Modifier
                    .offset(x.dp, y.dp)
                    .size((2 + index % 4).dp)
                    .background(
                        if (index % 3 == 0) PrimaryRed.copy(alpha = 0.6f)
                        else SecondaryPurple.copy(alpha = 0.4f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }

        // Light streak
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f * streakWidth)
                .height(2.dp)
                .alpha(streakAlpha * 0.8f)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryRed.copy(alpha = 0.8f),
                            Color(0xFFFF3D81).copy(alpha = 0.9f),
                            PrimaryRed.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(logoScale * pulseScale)
        ) {
            // KINO Text with play symbol in O
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                "KINO".forEachIndexed { index, char ->
                    val charDelay = 800 + (index * 150)
                    val charAlpha by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(600, delayMillis = charDelay),
                        label = "char$index"
                    )

                    val charScale by animateFloatAsState(
                        targetValue = 1f,
                        animationSpec = tween(800, delayMillis = charDelay, easing = EaseOutBack),
                        label = "charScale$index"
                    )

                    if (char == 'O') {
                        // Transform O into play symbol
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(charScale)
                                .alpha(charAlpha),
                            contentAlignment = Alignment.Center
                        ) {
                            // Outer glow ring
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                PrimaryRed.copy(alpha = 0.3f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )

                            // O with play triangle
                            Text(
                                text = "O",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.alpha(0.9f)
                            )

                            // Play triangle overlay
                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.size(20.dp)
                            ) {
                                drawPath(
                                    path = androidx.compose.ui.graphics.Path().apply {
                                        moveTo(0f, 0f)
                                        lineTo(size.width * 0.8f, size.height * 0.5f)
                                        lineTo(0f, size.height)
                                        close()
                                    },
                                    color = PrimaryRed
                                )
                            }
                        }
                    } else {
                        Text(
                            text = char.toString(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .scale(charScale)
                                .alpha(charAlpha),
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = Shadow(
                                    color = PrimaryRed.copy(alpha = 0.5f),
                                    offset = Offset(0f, 4f),
                                    blurRadius = 16f
                                )
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // "by Raj Karmakar"
            Text(
                text = "by Raj Karmakar",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = TextMuted,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }

        // Cinematic light pulse at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            PrimaryRed.copy(alpha = 0.3f * glowIntensity),
                            SecondaryPurple.copy(alpha = 0.2f * glowIntensity),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
