package com.rajkarmakar.kino.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rajkarmakar.kino.R
import com.rajkarmakar.kino.ui.screens.details.DetailsScreen
import com.rajkarmakar.kino.ui.screens.home.HomeScreen
import com.rajkarmakar.kino.ui.screens.intro.IntroScreen
import com.rajkarmakar.kino.ui.screens.library.LibraryScreen
import com.rajkarmakar.kino.ui.screens.player.PlayerScreen
import com.rajkarmakar.kino.ui.screens.profile.ProfileScreen
import com.rajkarmakar.kino.ui.screens.search.SearchScreen
import com.rajkarmakar.kino.ui.theme.Background
import com.rajkarmakar.kino.ui.theme.PrimaryRed
import com.rajkarmakar.kino.ui.theme.Surface
import com.rajkarmakar.kino.ui.theme.TextMuted
import com.rajkarmakar.kino.ui.theme.TextPrimary

// ============================================
// NAVIGATION ROUTES
// ============================================

sealed class Screen(val route: String, val title: String? = null, val icon: Int? = null) {
    object Intro : Screen("intro")
    object Home : Screen("home", "Home", R.drawable.ic_home)
    object Search : Screen("search", "Search", R.drawable.ic_search)
    object Library : Screen("library", "Library", R.drawable.ic_library)
    object Profile : Screen("profile", "Profile", R.drawable.ic_profile)
    object Details : Screen("details/{mediaId}")
    object Player : Screen("player/{mediaId}?episodeId={episodeId}")

    fun createRoute(vararg args: String): String {
        return when (this) {
            is Details -> "details/${args[0]}"
            is Player -> if (args.size > 1) "player/${args[0]}?episodeId=${args[1]}" else "player/${args[0]}"
            else -> route
        }
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.Library,
    Screen.Profile
)

// ============================================
// MAIN NAVIGATION
// ============================================

@Composable
fun KinoNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { 
        currentDestination?.route?.startsWith(it.route.split("/")[0]) == true 
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                KinoBottomNavigation(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        },
        containerColor = Background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Intro.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) + 
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(400)
                )
            }
        ) {
            composable(Screen.Intro.route) {
                IntroScreen(
                    onIntroComplete = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Intro.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onMediaClick = { mediaId ->
                        navController.navigate(Screen.Details.createRoute(mediaId))
                    }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    onMediaClick = { mediaId ->
                        navController.navigate(Screen.Details.createRoute(mediaId))
                    }
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    onMediaClick = { mediaId ->
                        navController.navigate(Screen.Details.createRoute(mediaId))
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onReplayIntro = {
                        navController.navigate(Screen.Intro.route) {
                            popUpTo(Screen.Profile.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("mediaId") { type = NavType.StringType })
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                DetailsScreen(
                    mediaId = mediaId,
                    onBackClick = { navController.popBackStack() },
                    onWatchClick = { id, episodeId ->
                        navController.navigate(Screen.Player.createRoute(id, episodeId ?: ""))
                    }
                )
            }

            composable(
                route = Screen.Player.route,
                arguments = listOf(
                    navArgument("mediaId") { type = NavType.StringType },
                    navArgument("episodeId") { 
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val mediaId = backStackEntry.arguments?.getString("mediaId") ?: ""
                val episodeId = backStackEntry.arguments?.getString("episodeId")
                PlayerScreen(
                    mediaId = mediaId,
                    episodeId = episodeId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// ============================================
// BOTTOM NAVIGATION
// ============================================

@Composable
fun KinoBottomNavigation(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    NavigationBar(
        containerColor = Surface.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { 
                it.route?.startsWith(screen.route.split("/")[0]) == true 
            } == true

            NavigationBarItem(
                icon = { 
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(
                            id = screen.icon ?: R.drawable.ic_home
                        ),
                        contentDescription = screen.title
                    )
                },
                label = { 
                    Text(
                        text = screen.title ?: "",
                        style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                    )
                },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryRed,
                    selectedTextColor = PrimaryRed,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
