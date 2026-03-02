package com.aventique.sephora.features.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeNavigation(
    onProductClick: (Long) -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(onProductClick = onProductClick)
    }
}
