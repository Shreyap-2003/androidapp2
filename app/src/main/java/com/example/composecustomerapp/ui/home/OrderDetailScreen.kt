package com.example.composecustomerapp.ui.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.ui.components.BlingBottomNavigation

@Composable
fun OrderDetailScreen(
    orderId: String,
    viewModel: OrderDetailViewModel = viewModel(factory = OrderDetailViewModel.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateBack: () -> Unit = {},
    onNavigateHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()

    LaunchedEffect(orderId, homeUiState.allProducts) {
        viewModel.loadOrderDetail(orderId, homeUiState.allProducts)
    }

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
                    onCartClick = onNavigateToCart,
                    onOrdersClick = { /* Already here */ }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onNavigateBack() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF6B5800),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Back",
                        color = Color(0xFF6B5800),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            uiState.order?.let { order ->
                item {
                    OrderDetailCard(order)
                }
            }
        }
    }
}

@Composable
fun OrderDetailCard(order: Order) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = order.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = order.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Text(
                text = "\u20B9 ${order.price}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF1F1F1))
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow("Order ID", "#${order.orderNumber}")
            Spacer(modifier = Modifier.height(12.dp))
            DetailRow("Ordered On", order.date)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status", color = Color.Gray, fontWeight = FontWeight.Medium)
                StatusBadge(status = order.status)
            }

            if (order.status == "ASSIGNED" && order.partnerName != null) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFF1F1F1))
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text(
                        text = "PARTNER ASSIGNED",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    DetailRow("Partner Name", order.partnerName)
                    
                    order.partnerPhoneNumber?.let { phone ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Partner Phone", color = Color.Gray, fontWeight = FontWeight.Medium)
                            Text(
                                text = phone,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6B5800),
                                modifier = Modifier.clickable { 
                                    val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

