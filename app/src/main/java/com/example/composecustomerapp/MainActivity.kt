package com.example.composecustomerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composecustomerapp.ui.home.BakeryScreen
import com.example.composecustomerapp.ui.home.CategoryScreen
import com.example.composecustomerapp.ui.home.CakesScreen
import com.example.composecustomerapp.ui.home.CakesViewModel
import com.example.composecustomerapp.ui.home.CartScreen
import com.example.composecustomerapp.ui.home.CookiesScreen
import com.example.composecustomerapp.ui.home.CookiesViewModel
import com.example.composecustomerapp.ui.home.CoolDrinksScreen
import com.example.composecustomerapp.ui.home.DairyProductScreen
import com.example.composecustomerapp.ui.home.DairyViewModel
import com.example.composecustomerapp.ui.home.FruitJuicesScreen
import com.example.composecustomerapp.ui.home.FruitJuicesViewModel
import com.example.composecustomerapp.ui.home.FruitsScreen
import com.example.composecustomerapp.ui.home.FruitsViewModel
import com.example.composecustomerapp.ui.home.FrozenFoodsScreen
import com.example.composecustomerapp.ui.home.FrozenFoodsViewModel
import com.example.composecustomerapp.ui.home.GroceryScreen
import com.example.composecustomerapp.ui.home.HomeScreen
import com.example.composecustomerapp.ui.home.HomeViewModel
import com.example.composecustomerapp.ui.home.InstantFoodsScreen
import com.example.composecustomerapp.ui.home.NoodlesScreen
import com.example.composecustomerapp.ui.home.NoodlesViewModel
import com.example.composecustomerapp.ui.home.OrderDetailScreen
import com.example.composecustomerapp.ui.home.OrderDetailViewModel
import com.example.composecustomerapp.ui.home.OrdersScreen
import com.example.composecustomerapp.ui.home.OrdersViewModel
import com.example.composecustomerapp.ui.home.RusksWafersScreen
import com.example.composecustomerapp.ui.home.RusksWafersViewModel
import com.example.composecustomerapp.ui.home.SearchScreen
import com.example.composecustomerapp.ui.home.SearchViewModel
import com.example.composecustomerapp.ui.home.SoftDrinksScreen
import com.example.composecustomerapp.ui.home.SoftDrinksViewModel
import com.example.composecustomerapp.ui.home.EnergyDrinksScreen
import com.example.composecustomerapp.ui.home.EnergyDrinksViewModel
import com.example.composecustomerapp.ui.home.SoupsScreen
import com.example.composecustomerapp.ui.home.SoupsViewModel
import com.example.composecustomerapp.ui.home.VegetablesScreen
import com.example.composecustomerapp.ui.home.VegetablesViewModel
import com.example.composecustomerapp.ui.login.LoginScreen
import com.example.composecustomerapp.ui.profile.ProfileScreen
import com.example.composecustomerapp.ui.register.RegisterScreen
import com.example.composecustomerapp.ui.theme.ComposeCustomerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCustomerAppTheme {
                val navController = rememberNavController()
                val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
                val ordersViewModel: OrdersViewModel = viewModel()
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
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
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
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
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            }
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
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
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
                        val orderDetailViewModel: OrderDetailViewModel = viewModel()
                        OrderDetailScreen(
                            orderId = orderId,
                            viewModel = orderDetailViewModel,
                            homeViewModel = homeViewModel,
                            ordersViewModel = ordersViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            }
                        )
                    }
                    composable("cart") {
                        CartScreen(
                            viewModel = homeViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("grocery") {
                        GroceryScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Dairy Products" -> navController.navigate("dairy")
                                    "Vegetables" -> navController.navigate("vegetables")
                                    "Fruits" -> navController.navigate("fruits")
                                }
                                println("Clicked subcategory: $subCategory")
                            }
                        )
                    }
                    composable("dairy") {
                        val dairyViewModel: DairyViewModel = viewModel(factory = DairyViewModel.Factory)
                        DairyProductScreen(
                            viewModel = dairyViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("vegetables") {
                        val vegetablesViewModel: VegetablesViewModel = viewModel(factory = VegetablesViewModel.Factory)
                        VegetablesScreen(
                            viewModel = vegetablesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("fruits") {
                        val fruitsViewModel: FruitsViewModel = viewModel(factory = FruitsViewModel.Factory)
                        FruitsScreen(
                            viewModel = fruitsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("cool_drinks") {
                        CoolDrinksScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
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
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("fruit_juices") {
                        val fruitJuicesViewModel: FruitJuicesViewModel = viewModel(factory = FruitJuicesViewModel.Factory)
                        FruitJuicesScreen(
                            viewModel = fruitJuicesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("energy_drinks") {
                        val energyDrinksViewModel: EnergyDrinksViewModel = viewModel(factory = EnergyDrinksViewModel.Factory)
                        EnergyDrinksScreen(
                            viewModel = energyDrinksViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("bakery") {
                        BakeryScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Cookies" -> navController.navigate("cookies")
                                    "Cakes" -> navController.navigate("cakes")
                                    "Rusks & Wafers" -> navController.navigate("rusks_wafers")
                                }
                                println("Clicked subcategory: $subCategory")
                            }
                        )
                    }
                    composable("cookies") {
                        val cookiesViewModel: CookiesViewModel = viewModel(factory = CookiesViewModel.Factory)
                        CookiesScreen(
                            viewModel = cookiesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("cakes") {
                        val cakesViewModel: CakesViewModel = viewModel(factory = CakesViewModel.Factory)
                        CakesScreen(
                            viewModel = cakesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("rusks_wafers") {
                        val rusksWafersViewModel: RusksWafersViewModel = viewModel(factory = RusksWafersViewModel.Factory)
                        RusksWafersScreen(
                            viewModel = rusksWafersViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("instant_foods") {
                        InstantFoodsScreen(
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            },
                            onSubCategoryClick = { subCategory ->
                                when (subCategory) {
                                    "Noodles" -> navController.navigate("noodles")
                                    "Soups" -> navController.navigate("soups")
                                    "Frozen Foods" -> navController.navigate("frozen_foods")
                                }
                                println("Clicked subcategory: $subCategory")
                            }
                        )
                    }
                    composable("noodles") {
                        val noodlesViewModel: NoodlesViewModel = viewModel(factory = NoodlesViewModel.Factory)
                        NoodlesScreen(
                            viewModel = noodlesViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("soups") {
                        val soupsViewModel: SoupsViewModel = viewModel(factory = SoupsViewModel.Factory)
                        SoupsScreen(
                            viewModel = soupsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("frozen_foods") {
                        val frozenFoodsViewModel: FrozenFoodsViewModel = viewModel(factory = FrozenFoodsViewModel.Factory)
                        FrozenFoodsScreen(
                            viewModel = frozenFoodsViewModel,
                            homeViewModel = homeViewModel,
                            onNavigateToLogin = {
                                navController.navigate("login")
                            },
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onNavigateToProfile = {
                                navController.navigate("profile")
                            },
                            onNavigateToCart = {
                                navController.navigate("cart")
                            },
                            onNavigateToOrders = {
                                navController.navigate("orders")
                            }
                        )
                    }
                    composable("login") {
                        LoginScreen(
                            onNavigateToRegister = {
                                navController.navigate("register")
                            },
                            onLoginSuccess = {
                                println("AuthDebug: onLoginSuccess triggered in MainActivity")
                                homeViewModel.setAuthenticated(true)
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
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
                            onNavigateHome = {
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            onNavigateToSearch = {
                                navController.navigate("search")
                            },
                            onLogout = {
                                homeViewModel.setAuthenticated(false)
                                navController.navigate("home") {
                                    popUpTo(navController.graph.id) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(
                        route = "category/{title}",
                        arguments = listOf(navArgument("title") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: ""
                        CategoryScreen(
                            categoryTitle = title,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
