package com.example.composecustomerapp.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.ui.home.HomeViewModel
import com.example.composecustomerapp.ui.components.BlingBottomNavigation

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    onNavigateHome: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        // ... dialog code ...
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(text = "Logout", fontWeight = FontWeight.Bold, color = Color.Black) },
            text = { Text(text = "Are you sure you want to logout from Bling?", color = Color.Black) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout(onLogout)
                    }
                ) {
                    Text("Logout", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        topBar = {
            ProfileTopBar(
                firstName = uiState.firstName,
                onLogoClick = onNavigateHome
            )
        },
        bottomBar = {
            if (homeUiState.isAuthenticated) {
                BlingBottomNavigation(
                    currentRoute = "profile",
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF9FAFB)) // Very light gray background
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image Section with circular background
                ProfileImageHeader(uiState.fullName, uiState.status)

                Spacer(modifier = Modifier.height(24.dp))

                // Side-by-side Info Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoCard(
                        label = "PHONE",
                        value = uiState.phoneNumber,
                        icon = Icons.Default.Phone,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        label = "ROLE",
                        value = uiState.role,
                        icon = Icons.Default.Person,
                        iconBg = Color(0xFFF3F4F6),
                        iconTint = Color(0xFF6B7280),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Location Card
                LocationCard(
                    location = uiState.location,
                    coordinates = uiState.coordinates
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Premium Member Card
                PremiumCard(
                    type = uiState.membershipType,
                    since = uiState.membershipSince
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Card
                LogoutCard(onClick = { showLogoutDialog = true })
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProfileTopBar(firstName: String, onLogoClick: () -> Unit) {
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
            
            Text(
                text = "Hi $firstName!",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileImageHeader(fullName: String, status: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEF3C7)), // Light beige/yellow circle
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(2.dp, Color(0xFFF59E00), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFF59E00),
                    modifier = Modifier.size(60.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            color = Color(0xFFA7F3D0), // Teal background
            shape = CircleShape
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF059669), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = status,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF065F46)
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun LocationCard(location: String, coordinates: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Apartment, contentDescription = null, tint = Color(0xFF0284C7))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("PRIMARY LOCATION", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontWeight = FontWeight.Bold)
                Text(location, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Coordinates", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(coordinates, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
            }
        }
    }
}

@Composable
fun PremiumCard(type: String, since: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Dark gray
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF854D0E), CircleShape), // Brownish circle
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(type, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Text(since, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun LogoutCard(onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color(0xFFB91C1C) // Red
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Logout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB91C1C)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
