package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.ui.components.BlingBottomNavigation
import com.example.composecustomerapp.ui.components.BlingYellow
import com.example.composecustomerapp.ui.components.ProductCard

@Composable
fun DairyProductScreen(
    viewModel: DairyViewModel = viewModel(factory = DairyViewModel.Factory),
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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Black)
            }
        } else {
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
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Dairy Products",
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

                items(uiState.products) { product ->
                    val quantity = homeUiState.cartItems[product.id] ?: 0
                    ProductCard(
                        product = product,
                        quantity = quantity,
                        onIncrement = { homeViewModel.updateCart(product.id, 1) },
                        onDecrement = { homeViewModel.updateCart(product.id, -1) }
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FreshFromFarmsBanner()
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun FreshFromFarmsBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BlingYellow)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fresh from Farms",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF422006)
                )
                Text(
                    text = "Get delivery within 10 mins or it's free!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF422006).copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = Color(0xFF422006).copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DairyProductScreenPreview() {
    DairyProductScreen()
}
