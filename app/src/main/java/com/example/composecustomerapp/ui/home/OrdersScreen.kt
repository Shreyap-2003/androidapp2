package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
//import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.composecustomerapp.ui.components.BlingBottomNavigation
import com.example.composecustomerapp.ui.components.BlingYellow

@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateHome: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrderDetail: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeTopBar(
                isAuthenticated = homeUiState.isAuthenticated,
                username = homeUiState.username,
                profileImageUrl = homeUiState.userProfileImageUrl,
                onSignInClick = {},
                onLogoClick = onNavigateHome,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            if (homeUiState.isAuthenticated) {
                BlingBottomNavigation(
                    currentRoute = "orders",
                    cartItemCount = homeUiState.cartTotalItems,
                    onHomeClick = onNavigateHome,
                    onSearchClick = onNavigateToSearch,
                    onCartClick = homeUiState.cartTotalItems.let { onNavigateToCart },
                    onOrdersClick = {}
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
        ) {
            Text(
                text = "My Orders",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            // Tab Toggle
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OrderTabChip(
                    text = "Active",
                    isSelected = uiState.selectedTab == OrderTab.ACTIVE,
                    onClick = { viewModel.setSelectedTab(OrderTab.ACTIVE) }
                )
                OrderTabChip(
                    text = "Completed",
                    isSelected = uiState.selectedTab == OrderTab.COMPLETED,
                    onClick = { viewModel.setSelectedTab(OrderTab.COMPLETED) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val orders = if (uiState.selectedTab == OrderTab.ACTIVE) uiState.activeOrders else uiState.completedOrders
                
                items(orders) { order ->
                    OrderCard(
                        order = order, 
                        isCompleted = uiState.selectedTab == OrderTab.COMPLETED,
                        onClick = { onNavigateToOrderDetail(order.id) }
                    )
                }

                if (uiState.selectedTab == OrderTab.ACTIVE) {
                    item {
                        CravingMoreCard(onShopNow = onNavigateHome)
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun OrderTabChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .height(40.dp)
            .width(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF6B5800) else Color(0xFFE5E7EB),
        contentColor = if (isSelected) Color.White else Color.Black
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun OrderCard(order: Order, isCompleted: Boolean, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORDER #${order.orderNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                
                StatusBadge(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F4F6))
                ) {
                    AsyncImage(
                        model = order.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "\u20B9 ${order.price}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "View Details",
                            color = Color(0xFF6B5800),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color(0xFF6B5800),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "ORDER #${order.orderNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.date,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val containerColor = when (status) {
        "IN_PROGRESS", "IN PROGRESS" -> Color(0xFFFFF9C4)
        "COMPLETED" -> Color(0xFFE6F4EA)
        "FAILED" -> Color(0xFFFFEBEE)
        "OPEN" -> Color(0xFFE3F2FD)
        else -> Color(0xFFE5E7EB)
    }
    val contentColor = when (status) {
        "IN_PROGRESS", "IN PROGRESS" -> Color(0xFF854D0E)
        "COMPLETED" -> Color(0xFF065F46)
        "FAILED" -> Color(0xFFB71C1C)
        "OPEN" -> Color(0xFF0D47A1)
        else -> Color.Black
    }
    
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(contentColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = status,
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CravingMoreCard(onShopNow: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1).copy(alpha = 0.5f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = BlingYellow
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = Color(0xFF854D0E)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Craving more?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Check out our latest offers on snacks and daily essentials.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.clickable(onClick = onShopNow),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Shop Now",
                    color = Color(0xFF6B5800),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF6B5800),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}
