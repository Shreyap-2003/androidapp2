package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.ui.components.BlingBottomNavigation
import com.example.composecustomerapp.ui.components.BlingYellow
import com.example.composecustomerapp.ui.components.ProductCard

@Composable
fun EnergyDrinksScreen(
    viewModel: EnergyDrinksViewModel = viewModel(factory = EnergyDrinksViewModel.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToLogin: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                isAuthenticated = homeUiState.isAuthenticated,
                username = homeUiState.username,
                profileImageUrl = homeUiState.userProfileImageUrl,
                onSignInClick = onNavigateToLogin,
                onLogoClick = onNavigateHome,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            if (homeUiState.isAuthenticated) {
                BlingBottomNavigation(
                    currentRoute = "home",
                    cartItemCount = homeUiState.cartTotalItems,
                    onHomeClick = onNavigateHome,
                    onSearchClick = onNavigateToSearch,
                    onCartClick = onNavigateToCart,
                    onOrdersClick = onNavigateToOrders
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Power Up Banner
            item(span = { GridItemSpan(maxLineSpan) }) {
                PowerUpBanner()
            }

            // Centered Title Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Energy Drinks",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(3.dp)
                            .background(BlingYellow, RoundedCornerShape(2.dp))
                    )
                }
            }

            // Products Grid
            items(uiState.products) { product ->
                val quantity = homeUiState.cartItems[product.id] ?: 0
                ProductCard(
                    product = product,
                    quantity = quantity,
                    onIncrement = { homeViewModel.updateCart(product.id, 1) },
                    onDecrement = { homeViewModel.updateCart(product.id, -1) }
                )
            }

            // More coming soon card
            item {
                EnergyComingSoonCard()
            }
            
            // Bottom Padding
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PowerUpBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlingYellow)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Power Up",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF422006)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Get your favorite energy\ndrinks delivered in minutes.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF422006).copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun EnergyComingSoonCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4).copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, BlingYellow.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color(0xFF854D0E),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "More coming soon!",
                color = Color(0xFF854D0E),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EnergyDrinksScreenPreview() {
    EnergyDrinksScreen()
}
