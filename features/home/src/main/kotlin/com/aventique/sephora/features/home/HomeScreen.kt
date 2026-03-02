package com.aventique.sephora.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.aventique.sephora.domain.common.SortOrder
import com.aventique.sephora.domain.model.Product
import com.aventique.sephora.domain.model.Review
import org.koin.androidx.compose.koinViewModel
import java.math.BigDecimal
import java.math.RoundingMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onProductClick: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onProductClick = onProductClick,
        toggleSortOrder = viewModel::toggleSortOrder,
        toggleExpanded = viewModel::toggleExpanded,
        onRefresh = viewModel::onRefresh,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onSearchQueryChange: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    toggleSortOrder: () -> Unit,
    toggleExpanded: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isLoading && uiState.error == null) {
                ExtendedFloatingActionButton(
                    onClick = toggleSortOrder,
                    icon = {
                        Icon(
                            imageVector = if (uiState.sortOrder == SortOrder.BEST_TO_WORST)
                                Icons.Default.KeyboardArrowDown
                            else
                                Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                        )
                    },
                    text = {
                        Text(
                            if (uiState.sortOrder == SortOrder.BEST_TO_WORST)
                                stringResource(R.string.home_sort_best_to_worst)
                            else
                                stringResource(R.string.home_sort_worst_to_best),
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                onClear = { onSearchQueryChange("") }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    uiState.error != null -> {
                        ErrorContent(
                            message = uiState.error,
                            onRetry = onRefresh,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    else -> {
                        ProductList(
                            products = uiState.products,
                            expandedProductId = uiState.expandedProductId,
                            onProductClick = { onProductClick(it) },
                            onRatingClick = toggleExpanded,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.home_search_placeholder)) },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.home_search_clear_cd)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
    )
}

@Composable
private fun ProductList(
    products: List<Product>,
    expandedProductId: Long?,
    onProductClick: (Long) -> Unit,
    onRatingClick: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                product = product,
                isExpanded = expandedProductId == product.id,
                onProductClick = { onProductClick(product.id) },
                onRatingClick = { onRatingClick(product.id) },
            )
        }
        item { Spacer(modifier = Modifier.height(48.dp)) }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    isExpanded: Boolean,
    onProductClick: () -> Unit,
    onRatingClick: () -> Unit,
) {
    val validRatingCount = product.reviews.count { it.rating != null && it.rating!! <= 5.0 }
    val hasExpandableReviews = !product.hideReviews && product.reviews.isNotEmpty()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = { onProductClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row: image + info
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    SubcomposeAsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.home_product_image_placeholder),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.brandName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.home_product_price,
                            "%.2f".format(product.price)
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Star rating row — clickable to expand/collapse reviews
                    Row(
                        modifier = if (hasExpandableReviews) Modifier.clickable { onRatingClick() } else Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        val averageRating = BigDecimal(product.averageRating)
                            .setScale(1, RoundingMode.HALF_UP)

                        StarRating(rating = averageRating.toDouble())
                        Text(
                            text = stringResource(
                                R.string.home_product_rating,
                                "%.1f".format(product.averageRating),
                                validRatingCount
                            ),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (hasExpandableReviews) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) stringResource(R.string.home_collapse_reviews_cd) else stringResource(
                                    R.string.home_expand_reviews_cd
                                ),
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                }
            }

            // Expandable reviews section
            AnimatedVisibility(visible = isExpanded && hasExpandableReviews) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(2.dp))
                    product.reviews.forEach { review ->
                        ReviewItem(review = review)
                    }
                }
            }
        }
    }
}

@Composable
private fun StarRating(
    rating: Double,
    maxStars: Int = 5
) {
    val filledCount = rating.toInt().coerceIn(0, maxStars)
    val hasHalf = (rating - filledCount) >= 0.5 && filledCount < maxStars

    Row {
        repeat(filledCount) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (hasHalf) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.StarHalf,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        repeat(maxStars - filledCount - if (hasHalf) 1 else 0) {
            Icon(
                imageVector = Icons.Outlined.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun ReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = review.reviewerName ?: stringResource(R.string.home_anonymous_reviewer),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
            if (review.rating != null) {
                Text(
                    text = stringResource(
                        R.string.home_review_rating,
                        "%.1f".format(review.rating)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = review.text.ifBlank { "-" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.home_error_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.home_retry))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val sampleProducts = listOf(
        Product(
            id = 1,
            name = "Lipstick",
            brandName = "BrandA",
            price = 12.5,
            averageRating = 4.3,
            reviews = listOf(
                Review(reviewerName = "Alice", rating = 5.0, text = "Love it!"),
                Review(reviewerName = "Bob", rating = 4.0, text = "Good shade.")
            ),
            hideReviews = false,
            description = "Sample description",
            thumbnailUrl = "https://picsum.photos/150/150",
            imageUrl = "https://picsum.photos/600/600",
            isProductSet = false,
            isSpecialBrand = false,
        ),
        Product(
            id = 2,
            name = "Eyeliner",
            brandName = "BrandB",
            price = 8.0,
            averageRating = 3.8,
            reviews = emptyList(),
            hideReviews = true,
            description = "Another sample description",
            thumbnailUrl = "https://picsum.photos/150/150",
            imageUrl = "https://picsum.photos/600/600",
            isProductSet = true,
            isSpecialBrand = false,
        )
    )

    val uiState = HomeUiState(
        products = sampleProducts,
        isLoading = false,
        error = null,
        searchQuery = "",
        sortOrder = SortOrder.BEST_TO_WORST,
        expandedProductId = null,
    )

    HomeScreenContent(
        uiState = uiState,
        onSearchQueryChange = {},
        onProductClick = {},
        toggleSortOrder = {},
        toggleExpanded = {},
        onRefresh = {},
    )
}