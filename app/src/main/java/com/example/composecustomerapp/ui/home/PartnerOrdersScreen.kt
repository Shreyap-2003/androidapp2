package com.example.composecustomerapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.composecustomerapp.data.model.Order

@Composable
fun PartnerOrdersScreen(
    viewModel: PartnerOrdersViewModel = viewModel(factory = PartnerOrdersViewModel.Factory),
    partnerHomeViewModel: PartnerHomeViewModel = viewModel(factory = PartnerHomeViewModel.Factory),
    onNavigateHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by partnerHomeViewModel.uiState.collectAsState()
    val completedOrders = viewModel.completedOrdersPaged.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            PartnerTopBar(
                username = homeUiState.username,
                onProfileClick = onNavigateToProfile
            )
        },
        bottomBar = {
            PartnerBottomNavigation(
                currentRoute = "orders",
                onHomeClick = onNavigateHome,
                onOrdersClick = { /* Already here */ }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF9FAFB)),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Completed Orders",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }

            items(count = completedOrders.itemCount) { index ->
                val order = completedOrders[index]
                if (order != null) {
                    CompletedOrderCard(order)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                completedOrders.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Black)
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.Black)
                            }
                        }
                        loadState.refresh is LoadState.Error -> {
                            val e = completedOrders.loadState.refresh as LoadState.Error
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = e.error.localizedMessage ?: "Error loading orders",
                                    color = Color.Red,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Button(onClick = { retry() }) {
                                    Text("Retry")
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val e = completedOrders.loadState.append as LoadState.Error
                            Text(
                                text = e.error.localizedMessage ?: "Error loading more orders",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        completedOrders.itemCount == 0 && loadState.refresh is LoadState.NotLoading -> {
                            Box(
                                modifier = Modifier.fillParentMaxSize().padding(top = 100.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No completed orders yet", color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Square Image with border
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF9FAFB)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = order.imageUrl,
                    contentDescription = order.name,
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "\u20B9 ${order.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Text(
                    text = "Order ID: #${order.orderNumber}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Completed Badge
                Surface(
                    color = Color(0xFFE6F4EA),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(1.dp, Color(0xFF065F46).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF065F46))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Completed",
                            color = Color(0xFF065F46),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
