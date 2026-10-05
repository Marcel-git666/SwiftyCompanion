package com.example.swiftycompanion.ui.navigation

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.swiftycompanion.ui.profile.ProfileScreen
import com.example.swiftycompanion.ui.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

@Serializable
data class ProfileRoute(val login: String)

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SearchRoute,
        modifier = modifier,
    ) {
        composable<SearchRoute> {
            SearchScreen(
                onUserFound = { user -> navController.navigate(ProfileRoute(login = user.login)) },
                modifier = Modifier.safeDrawingPadding(),
            )
        }
        composable<ProfileRoute> {
            ProfileScreen(onBack = { navController.popBackStack() })
        }
    }
}