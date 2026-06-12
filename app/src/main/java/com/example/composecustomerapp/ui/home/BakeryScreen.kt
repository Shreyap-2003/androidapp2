package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.composecustomerapp.ui.components.SubCategoryCard

@Composable
fun BakeryScreen(
    viewModel: BakeryViewModel = viewModel(factory = BakeryViewModel.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateToLogin: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onSubCategoryClick: (String) -> Unit = {}
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
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF9FAFB)),
                contentPadding = PaddingValues(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Bakery",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                        Text(
                            text = "Freshly baked delights delivered in minutes.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }

                items(uiState.subCategories) { subCategory ->
                    SubCategoryCard(subCategory, onClick = { onSubCategoryClick(subCategory.title) })
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BakeryScreenPreview() {
    BakeryScreen()
}
