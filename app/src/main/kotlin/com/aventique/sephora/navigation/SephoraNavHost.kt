package com.aventique.sephora.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.aventique.sephora.features.detail.DetailRoute
import com.aventique.sephora.features.detail.detailNavigation
import com.aventique.sephora.features.home.HomeRoute
import com.aventique.sephora.features.home.homeNavigation

@Composable
fun SephoraNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
    ) {
        homeNavigation(
            onProductClick = { productId ->
                navController.navigate(DetailRoute(productId))
            },
        )
        detailNavigation(
            onBack = {
                navController.navigateUp()
            },
        )
    }
}
