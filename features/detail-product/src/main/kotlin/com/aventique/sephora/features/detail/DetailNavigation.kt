package com.aventique.sephora.features.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(val productId: Long)

fun NavGraphBuilder.detailNavigation(
    onBack: () -> Unit,
) {
    composable<DetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<DetailRoute>()
        DetailScreen(
            productId = route.productId,
            onBack = onBack,
        )
    }
}
