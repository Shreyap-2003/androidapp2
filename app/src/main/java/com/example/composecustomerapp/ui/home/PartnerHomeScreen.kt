package com.example.composecustomerapp.ui.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.ui.components.BlingYellow
import com.example.composecustomerapp.ui.components.shimmerEffect
import com.example.composecustomerapp.util.NotificationBus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerHomeScreen(
    viewModel: PartnerHomeViewModel = viewModel(factory = PartnerHomeViewModel.Factory),
    onNavigateToLogin: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    val openOrders = viewModel.openOrdersPaged.collectAsLazyPagingItems()
    
    var incomingOrder by remember { mutableStateOf<Order?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // Listen for new orders via FCM or Notification click
    LaunchedEffect(Unit) {
        println("AuthDebug: PartnerHomeScreen listener active and collecting")
        NotificationBus.newOrderEvents.collect { order ->
            println("AuthDebug: PartnerHomeScreen received order via flow: ${order.id}")
            incomingOrder = order
            showSheet = true
        }
    }

    // Reset replay cache when the sheet is successfully shown/hidden to prevent duplicate popups
    LaunchedEffect(showSheet) {
        if (!showSheet) {
            NotificationBus.clear()
        }
    }

    if (showSheet && incomingOrder != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                incomingOrder = null
            },
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "New Order Alert!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Customer Detail Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PartnerInfoRow(icon = Icons.Default.Person, text = incomingOrder!!.customerName ?: "Customer")
                    PartnerInfoRow(icon = Icons.Default.LocationOn, text = incomingOrder!!.customerAddress ?: "No Address")
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Product Detail Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = incomingOrder!!.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = incomingOrder!!.name, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = "\u20B9 ${incomingOrder!!.price}", style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        viewModel.acceptOrder(incomingOrder!!.id) { success, error ->
                            if (success) {
                                Toast.makeText(context, "Order accepted successfully!", Toast.LENGTH_SHORT).show()
                                showSheet = false
                            } else {
                                Toast.makeText(context, "Error: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006D4E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Accept Order", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            PartnerTopBar(
                username = uiState.username,
                onProfileClick = onNavigateToProfile
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            PartnerBottomNavigation(
                currentRoute = "home",
                onHomeClick = { /* Already here */ },
                onOrdersClick = onNavigateToOrders
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshData() },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(width = 200.dp, height = 32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .shimmerEffect()
                        )
                    }
                    items(3) {
                        PartnerOrderSkeleton()
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF9FAFB)),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp)
                ) {
                    item {
                        Text(
                            text = "Welcome Partner ${uiState.username}!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (uiState.acceptedOrders.isNotEmpty()) {
                        // Show ONLY accepted orders when there are any
                        items(uiState.acceptedOrders) { order ->
                            PartnerOrderCard(
                                order = order,
                                isAccepted = true,
                                onActionClick = { 
                                    viewModel.completeOrder(order.id) { success, error ->
                                        if (success) {
                                            Toast.makeText(context, "Order completed successfully \u2705", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Error: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        // Show Available Orders only if no order is accepted
                        // Available Orders Header
                        item {
                            Text(
                                text = "Available Orders",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }

                        when {
                            openOrders.loadState.refresh is LoadState.Loading -> {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color.Black)
                                    }
                                }
                            }
                            openOrders.loadState.refresh is LoadState.Error -> {
                                val e = openOrders.loadState.refresh as LoadState.Error
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Error: ${e.error.localizedMessage}", color = Color.Red)
                                        Button(onClick = { openOrders.retry() }) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }
                            openOrders.itemCount == 0 -> {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                        Text("No open orders at the moment", color = Color.Gray)
                                    }
                                }
                            }
                            else -> {
                                items(count = openOrders.itemCount) { index ->
                                    val order = openOrders[index]
                                    if (order != null) {
                                        PartnerOrderCard(
                                            order = order,
                                            isAccepted = false,
                                            onActionClick = {
                                                viewModel.acceptOrder(order.id) { success, error ->
                                                    if (success) {
                                                        Toast.makeText(context, "Order accepted!", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(context, "Error: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }

                                if (openOrders.loadState.append is LoadState.Loading) {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PartnerTopBar(username: String, onProfileClick: () -> Unit) {
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
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "Bling",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Hi $username!",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = onProfileClick) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PartnerOrderCard(order: Order, isAccepted: Boolean, onActionClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order #${order.orderNumber}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Surface(
                    color = if (isAccepted) Color(0xFFFFF7E6) else Color(0xFFFFEFD5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isAccepted) "IN_PROGRESS" else "OPEN",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = if (isAccepted) Color(0xFFEA580C) else Color(0xFFD97706),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PartnerInfoRow(icon = Icons.Default.Person, text = order.customerName ?: "Unknown Customer")
                PartnerInfoRow(
                    icon = Icons.Default.Phone, 
                    text = order.customerPhoneNumber ?: "No Phone",
                    onClick = {
                        order.customerPhoneNumber?.let { phone ->
                            val intent = Intent(Intent.ACTION_DIAL, "tel:$phone".toUri())
                            context.startActivity(intent)
                        }
                    }
                )
                PartnerInfoRow(
                    icon = Icons.Default.LocationOn, 
                    text = order.customerAddress ?: "No Address",
                    onClick = {
                        val lat = order.customerLatitude
                        val lng = order.customerLongitude
                        if (lat != null && lng != null) {
                            // Using standard Google Maps URL API for destination navigation
                            val mapIntentUri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng".toUri()
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapIntentUri)
                            context.startActivity(mapIntent)
                        } else {
                            order.customerAddress?.let { address ->
                                val gmmIntentUri = "geo:0,0?q=${android.net.Uri.encode(address)}".toUri()
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            }
                        }
                    }
                )
            }
            
            if (isAccepted) {
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Square Image with border
                    Box(
                        modifier = Modifier
                            .size(80.dp)
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

                    Column {
                        Text(
                            text = order.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "\u20B9 ${order.price}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAccepted) Color(0xFF2E7D32) else Color(0xFF006D4E)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isAccepted) "Complete" else "Accept Order",
                    color = Color.White, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun PartnerOrderSkeleton() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(120.dp, 24.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                Box(modifier = Modifier.size(80.dp, 24.dp).clip(RoundedCornerShape(12.dp)).shimmerEffect())
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).shimmerEffect())
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(modifier = Modifier.size(150.dp, 20.dp).clip(RoundedCornerShape(4.dp)).shimmerEffect())
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
fun PartnerInfoRow(icon: ImageVector, text: String, onClick: (() -> Unit)? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF8B4513),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (onClick != null) Color(0xFF6B5800) else Color.Black
        )
    }
}

@Composable
fun PartnerBottomNavigation(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                indicatorColor = BlingYellow
            )
        )
        NavigationBarItem(
            selected = currentRoute == "orders",
            onClick = onOrdersClick,
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Completed") },
            label = { Text("Completed") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                indicatorColor = BlingYellow
            )
        )
    }
}
