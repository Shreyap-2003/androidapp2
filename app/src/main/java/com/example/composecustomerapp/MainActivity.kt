package com.example.composecustomerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.util.NotificationBus
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composecustomerapp.ui.home.*
import com.example.composecustomerapp.ui.login.LoginScreen
import com.example.composecustomerapp.ui.profile.ProfileScreen
import com.example.composecustomerapp.ui.register.RegisterScreen
import com.example.composecustomerapp.ui.theme.ComposeCustomerAppTheme

class MainActivity : ComponentActivity() {

    private var currentIntent by mutableStateOf<Intent?>(null)
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            println("AuthDebug: Notification permission granted")
        } else {
            println("AuthDebug: Notification permission denied")
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        currentIntent = intent
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentIntent = intent
        enableEdgeToEdge()
        
        // Ask for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ComposeCustomerAppTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                val ordersViewModel: OrdersViewModel = viewModel(factory = OrdersViewModel.Factory)
                
                // Handle notification click navigation
                LaunchedEffect(currentIntent) {
                    val intent = currentIntent ?: return@LaunchedEffect
                    if (intent.getBooleanExtra("OPEN_PARTNER_HOME", false)) {
                        println("AuthDebug: Notification clicked, handling navigation to partner_home")

                        val orderId = intent.getStringExtra("orderId")
                        val incomingOrder = if (orderId != null) {
                            Order(
                                id = orderId,
                                orderNumber = orderId,
                                name = intent.getStringExtra("itemName") ?: "Bling Order",
                                price = intent.getStringExtra("price")?.toDoubleOrNull() ?: 0.0,
                                status = "OPEN",
                                imageUrl = intent.getStringExtra("imageUrl") ?: "",
                                date = "Just now",
                                customerName = intent.getStringExtra("customerName") ?: "New Customer",
                                customerPhoneNumber = intent.getStringExtra("customerPhone") ?: "",
                                customerAddress = intent.getStringExtra("address") ?: "Check Dashboard"
                            )
                        } else null

                        // 1. Navigate to ensure we are on the dashboard
                        navController.navigate("partner_home") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }

                        // 2. Emit the order to trigger the bottom sheet
                        incomingOrder?.let {
                            println("AuthDebug: Emitting order ${it.id} to NotificationBus")
                            NotificationBus.emitOrder(it)
                        }

                        // 3. Reset the intent state
                        currentIntent = null
                    }
                }
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateToCategory = { categoryTitle ->
                                when (categoryTitle) {
                                    "Grocery" -> navController.navigate("grocery")
                                    "Cool Drinks & Juices" -> navController.navigate("cool_drinks")
                                    "Bakery" -> navController.navigate("bakery")
                                    "Instant Foods" -> navController.navigate("instant_foods")
                                    else -> navController.navigate("category/$categoryTitle")
                                }
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onNavigateToOrderDetail = { orderId ->
                                navController.navigate("order_detail/$orderId")
                            }
                        )
                    }
                    composable("search") {
                        val searchViewModel: SearchViewModel = viewModel()
                        SearchScreen(
                            viewModel = searchViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onNavigateToProfile = { navController.navigate("profile") }
                        )
                    }
                    composable("orders") {
                        OrdersScreen(
                            viewModel = ordersViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToOrderDetail = { orderId ->
                                navController.navigate("order_detail/$orderId")
                            }
                        )
                    }
                    composable(
                        route = "order_detail/{orderId}",
                        arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                        val orderDetailViewModel: OrderDetailViewModel = viewModel(factory = OrderDetailViewModel.Factory)
                        OrderDetailScreen(
                            orderId = orderId,
                            viewModel = orderDetailViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToProfile = { navController.navigate("profile") }
                        )
                    }
                    composable("cart") {
                        CartScreen(
                            viewModel = homeViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("grocery") {
                        GroceryScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Dairy Products" -> navController.navigate("dairy")
                                    "Vegetables" -> navController.navigate("vegetables")
                                    "Fruits" -> navController.navigate("fruits")
                                }
                            }
                        )
                    }
                    composable("dairy") {
                        val dairyViewModel: DairyViewModel = viewModel(factory = DairyViewModel.Factory)
                        DairyProductScreen(
                            viewModel = dairyViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("vegetables") {
                        val vegetablesViewModel: VegetablesViewModel = viewModel(factory = VegetablesViewModel.Factory)
                        VegetablesScreen(
                            viewModel = vegetablesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("fruits") {
                        val fruitsViewModel: FruitsViewModel = viewModel(factory = FruitsViewModel.Factory)
                        FruitsScreen(
                            viewModel = fruitsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("cool_drinks") {
                        CoolDrinksScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Soft Drinks" -> navController.navigate("soft_drinks")
                                    "Fruit Juices" -> navController.navigate("fruit_juices")
                                    "Energy Drinks" -> navController.navigate("energy_drinks")
                                }
                            }
                        )
                    }
                    composable("soft_drinks") {
                        val softDrinksViewModel: SoftDrinksViewModel = viewModel(factory = SoftDrinksViewModel.Factory)
                        SoftDrinksScreen(
                            viewModel = softDrinksViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("fruit_juices") {
                        val fruitJuicesViewModel: FruitJuicesViewModel = viewModel(factory = FruitJuicesViewModel.Factory)
                        FruitJuicesScreen(
                            viewModel = fruitJuicesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("energy_drinks") {
                        val energyDrinksViewModel: EnergyDrinksViewModel = viewModel(factory = EnergyDrinksViewModel.Factory)
                        EnergyDrinksScreen(
                            viewModel = energyDrinksViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("bakery") {
                        BakeryScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Cookies" -> navController.navigate("cookies")
                                    "Cakes" -> navController.navigate("cakes")
                                    "Rusks & Wafers" -> navController.navigate("rusks_wafers")
                                }
                            }
                        )
                    }
                    composable("cookies") {
                        val cookiesViewModel: CookiesViewModel = viewModel(factory = CookiesViewModel.Factory)
                        CookiesScreen(
                            viewModel = cookiesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("cakes") {
                        val cakesViewModel: CakesViewModel = viewModel(factory = CakesViewModel.Factory)
                        CakesScreen(
                            viewModel = cakesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("rusks_wafers") {
                        val rusksWafersViewModel: RusksWafersViewModel = viewModel(factory = RusksWafersViewModel.Factory)
                        RusksWafersScreen(
                            viewModel = rusksWafersViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("instant_foods") {
                        InstantFoodsScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Noodles" -> navController.navigate("noodles")
                                    "Soups" -> navController.navigate("soups")
                                    "Frozen Foods" -> navController.navigate("frozen_foods")
                                }
                            }
                        )
                    }
                    composable("noodles") {
                        val noodlesViewModel: NoodlesViewModel = viewModel(factory = NoodlesViewModel.Factory)
                        NoodlesScreen(
                            viewModel = noodlesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("soups") {
                        val soupsViewModel: SoupsViewModel = viewModel(factory = SoupsViewModel.Factory)
                        SoupsScreen(
                            viewModel = soupsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("frozen_foods") {
                        val frozenFoodsViewModel: FrozenFoodsViewModel = viewModel(factory = FrozenFoodsViewModel.Factory)
                        FrozenFoodsScreen(
                            viewModel = frozenFoodsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = { navController.navigate("login") },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToCart = { navController.navigate("cart") },
                            onNavigateToOrders = { navController.navigate("orders") }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = { navController.navigate("register") },
                            onLoginSuccess = { userType ->
                                try {
                                    homeViewModel.setAuthenticated(true)
                                    val startDestination = if (userType == "PARTNER") "partner_home" else "home"
                                    navController.navigate(startDestination) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    println("AuthDebug: Navigation error: ${e.message}")
                                }
                            },
                            onLogoClick = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onNavigateBack = {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onLogoClick = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            homeViewModel = homeViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = { navController.navigate("search") },
                            onNavigateToPartnerHome = {
                                navController.navigate("partner_home") {
                                    popUpTo("partner_home") { inclusive = true }
                                }
                            },
                            onNavigateToPartnerOrders = { navController.navigate("partner_orders") },
                            onLogout = {
                                homeViewModel.setAuthenticated(false)
                                navController.navigate("home") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("partner_home") {
                        PartnerHomeScreen(
                            onNavigateToLogin = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onNavigateToProfile = { navController.navigate("profile") },
                            onNavigateToOrders = { navController.navigate("partner_orders") }
                        )
                    }
                    composable("partner_orders") {
                        PartnerOrdersScreen(
                            onNavigateHome = {
                                navController.navigate("partner_home") {
                                    popUpTo("partner_home") { inclusive = true }
                                }
                            },
                            onNavigateToProfile = { navController.navigate("profile") }
                        )
                    }
                    composable(
                        route = "category/{title}",
                        arguments = listOf(navArgument("title") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: ""
                        CategoryScreen(
                            categoryTitle = title,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
