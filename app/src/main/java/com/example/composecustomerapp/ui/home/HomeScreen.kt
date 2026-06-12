package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
//import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.composecustomerapp.ui.components.BlingBottomNavigation
import com.example.composecustomerapp.ui.components.BlingYellow

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToCategory: (String) -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToOrderDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                isAuthenticated = uiState.isAuthenticated,
                username = uiState.username,
                profileImageUrl = uiState.userProfileImageUrl,
                onSignInClick = onNavigateToLogin,
                onLogoClick = onNavigateHome,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            if (uiState.isAuthenticated) {
                BlingBottomNavigation(
                    currentRoute = "home",
                    cartItemCount = uiState.cartTotalItems,
                    onHomeClick = onNavigateHome,
                    onSearchClick = onNavigateToSearch,
                    onCartClick = onNavigateToCart,
                    onOrdersClick = onNavigateToOrders
                )
            }
        },
//        floatingActionButton = {
//            if (uiState.isAuthenticated) {
//                FloatingActionButton(
//                    onClick = { /* Open Search */ },
//                    containerColor = Color(0xFF6B5800),
//                    contentColor = Color.White,
//                    shape = CircleShape,
//                    modifier = Modifier.padding(bottom = 16.dp)
//                ) {
//                    Icon(Icons.Default.Search, contentDescription = "Search")
//                }
//            }
//        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Welcome to Bling!",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 32.sp,
                            lineHeight = 40.sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lightning fast delivery at your doorstep.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Active Orders Section
            if (uiState.isAuthenticated && uiState.activeOrders.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Your Active Orders",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        TextButton(onClick = onNavigateToOrders) {
                            Text(
                                "View all",
                                color = Color(0xFF6B5800),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                uiState.activeOrders.forEach { order ->
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ActiveOrderCard(order, onClick = { onNavigateToOrderDetail(order.id) })
                    }
                }
            }

            // Shop by Category Title
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "Shop by Category",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            // Categories Grid Items
            items(uiState.categories) { category ->
                CategoryItem(category, onClick = { onNavigateToCategory(category.title) })
            }
        }
    }
}

@Composable
fun HomeTopBar(
    isAuthenticated: Boolean,
    username: String,
    profileImageUrl: String,
    onSignInClick: () -> Unit,
    onLogoClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val darkOlive = Color(0xFF5D4E00)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = darkOlive
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.GridView,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp).clickable { onLogoClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Bling",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.clickable { onLogoClick() }
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isAuthenticated) {
                Row(
                    modifier = Modifier.clickable(onClick = onProfileClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hi $username!",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = onSignInClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = CircleShape,
                    modifier = Modifier.height(44.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveOrderCard(order: Order, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = order.imageUrl,
                contentDescription = order.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "Order #${order.id}  \u20B9 ${order.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            StatusChip(status = order.status)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor) = when (status) {
        "ASSIGNED" -> Color(0xFFE6F4EA) to Color(0xFF1E8E3E)
        "IN PROGRESS" -> Color(0xFFFFF7E6) to Color(0xFFD97706)
        else -> Color.LightGray to Color.DarkGray
    }
    Surface(
        color = backgroundColor,
        shape = CircleShape,
        modifier = Modifier.border(1.dp, textColor.copy(alpha = 0.5f), CircleShape)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (status == "IN PROGRESS") {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(textColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun CategoryItem(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = category.imageUrl,
                contentDescription = category.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = category.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = category.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
