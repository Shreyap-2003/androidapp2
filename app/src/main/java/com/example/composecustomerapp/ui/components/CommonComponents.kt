package com.example.composecustomerapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val BlingYellow = Color(0xFFFFC107)
val LightGrayBackground = Color(0xFFF7F8F9)
val BlingGreen = Color(0xFF006D4E)

data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val unit: String? = null,
    val tag: String? = null
)

data class SubCategory(
    val title: String,
    val description: String,
    val imageUrl: String
)

@Composable
fun BlingLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Color.Gray,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BlingTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                color = Color.LightGray
            )
        },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = LightGrayBackground,
            unfocusedContainerColor = LightGrayBackground,
            disabledContainerColor = LightGrayBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        singleLine = true
    )
}

@Composable
fun BlingBottomNavigation(
    currentRoute: String,
    cartItemCount: Int = 0,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit,
    onOrdersClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        BottomNavItem("Home", Icons.Default.Home, currentRoute == "home", onHomeClick)
        
        BottomNavItem("Search", Icons.Default.Search, currentRoute == "search", onSearchClick)

        // Cart Item with Badge
        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = onCartClick,
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(containerColor = BlingYellow, contentColor = Color.Black) {
                                Text(cartItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            },
            label = { Text("Cart") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = BlingYellow
            )
        )

        NavigationBarItem(
            selected = currentRoute == "orders",
            onClick = onOrdersClick,
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
            label = { Text("Orders") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = BlingYellow
            )
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color.Black,
            selectedTextColor = Color.Black,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = BlingYellow
        )
    )
}

@Composable
fun AddToCartButton(
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (quantity == 0) {
        Button(
            onClick = onIncrement,
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlingGreen,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("ADD", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    } else {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(8.dp),
            color = BlingGreen,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDecrement, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                    text = quantity.toString(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                IconButton(onClick = onIncrement, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(1.dp, Color(0xFFF1F1F1), RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(horizontalAlignment = Alignment.Start) { // Changed to Start for Fruits alignment
                if (product.tag != null) {
                    Text(
                        text = product.tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF006D4E),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (product.unit != null) {
                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = "\u20B9 ${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            AddToCartButton(
                quantity = quantity,
                onIncrement = onIncrement,
                onDecrement = onDecrement
            )
        }
    }
}

@Composable
fun SubCategoryCard(subCategory: SubCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = subCategory.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Gradient Overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp)
            ) {
                Text(
                    text = subCategory.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subCategory.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
