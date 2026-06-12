package com.example.composecustomerapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.composecustomerapp.MainApplication
import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.repository.CategoryRepository
import com.example.composecustomerapp.data.repository.ItemRepository
import com.example.composecustomerapp.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Category(
    val title: String,
    val description: String,
    val imageUrl: String
)

data class Order(
    val id: String,
    val orderNumber: String,
    val name: String,
    val price: Int,
    val status: String,
    val imageUrl: String,
    val date: String
)

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val activeOrders: List<Order> = emptyList(),
    val isAuthenticated: Boolean = false,
    val username: String = "Shreya",
    val userProfileImageUrl: String = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&q=80&w=100",
    val cartItems: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val allProducts: List<com.example.composecustomerapp.ui.components.Product> = emptyList()
) {
    val cartTotalItems: Int get() = cartItems.values.sum()
    
    val cartProducts: List<Pair<com.example.composecustomerapp.ui.components.Product, Int>>
        get() = cartItems.mapNotNull { (id, qty) ->
            allProducts.find { it.id == id }?.let { it to qty }
        }
    
    val cartSubtotal: Int get() = cartProducts.sumOf { it.first.price * it.second }
}

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
        fetchCategories()
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
        viewModelScope.launch {
            val result = itemRepository.getItems()
            result.onSuccess { itemResponses ->
                val products = itemResponses.map {
                    com.example.composecustomerapp.ui.components.Product(
                        id = it.id.toString(),
                        name = it.name,
                        price = it.price.toInt(),
                        imageUrl = it.imageUrl
                    )
                }
                _uiState.update { it.copy(allProducts = products) }
            }
        }
    }

    fun placeOrder(productId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userIdStr = tokenManager.userId.first()
            if (userIdStr != null) {
                val userId = userIdStr.toIntOrNull()
                val itemId = productId.toIntOrNull()
                
                if (userId != null && itemId != null) {
                    val result = orderRepository.placeOrder(userId, itemId)
                    result.onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        removeItemFromCart(productId)
                        onSuccess()
                    }.onFailure { e ->
                        _uiState.update { it.copy(isLoading = false) }
                        onError(e.message ?: "Failed to place order")
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    onError("Invalid user or product ID")
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                onError("User not logged in")
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = categoryRepository.getCategories()
            result.onSuccess { categoryResponses ->
                val categories = categoryResponses.map {
                    val fullImageUrl = if (it.imageUrl.startsWith("http")) {
                        it.imageUrl
                    } else {
                        "http://10.200.24.230:8080/${it.imageUrl}"
                    }
                    Category(
                        title = it.name,
                        description = it.description,
                        imageUrl = fullImageUrl
                    )
                }
                _uiState.update { it.copy(categories = categories, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadHomeData() {
        val products = listOf(
            // Dairy
            com.example.composecustomerapp.ui.components.Product("p1", "Milk", 27, "https://images.unsplash.com/photo-1550583724-125581fe2f8a?auto=format&fit=crop&q=80&w=400", "FRESH DAILY"),
            com.example.composecustomerapp.ui.components.Product("p2", "Cheese", 40, "https://images.unsplash.com/photo-1486297678162-ad2a19b05840?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("p3", "Paneer", 80, "https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&q=80&w=400"),
            // Vegetables
            com.example.composecustomerapp.ui.components.Product("v1", "Tomato", 24, "https://images.unsplash.com/photo-1546473422-21f622c07044?auto=format&fit=crop&q=80&w=400", "1 kg", "FRESH"),
            com.example.composecustomerapp.ui.components.Product("v2", "Potato", 30, "https://images.unsplash.com/photo-1518977676601-b53f82aba655?auto=format&fit=crop&q=80&w=400", "1 kg"),
            com.example.composecustomerapp.ui.components.Product("v3", "Onion", 45, "https://images.unsplash.com/photo-1508747703725-719777637510?auto=format&fit=crop&q=80&w=400", "1 kg"),
            // Fruits
            com.example.composecustomerapp.ui.components.Product("f1", "Apple", 45, "https://images.unsplash.com/photo-1610832958506-aa56368176cf?auto=format&fit=crop&q=80&w=400", "1 kg"),
            com.example.composecustomerapp.ui.components.Product("f2", "Banana", 35, "https://images.unsplash.com/photo-1603833665858-e61d17a86224?auto=format&fit=crop&q=80&w=400", "12 pcs"),
            com.example.composecustomerapp.ui.components.Product("f3", "Orange", 40, "https://images.unsplash.com/photo-1547514701-42782101795e?auto=format&fit=crop&q=80&w=400", "500 g"),
            // Soft Drinks
            com.example.composecustomerapp.ui.components.Product("sd1", "Coca-Cola", 39, "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&q=80&w=400", "250 ml"),
            com.example.composecustomerapp.ui.components.Product("sd2", "Sprite", 35, "https://images.unsplash.com/photo-1624517452488-04869289c4ca?auto=format&fit=crop&q=80&w=400", "250 ml"),
            com.example.composecustomerapp.ui.components.Product("sd3", "Fanta", 38, "https://images.unsplash.com/photo-1624517452488-04869289c4ca?auto=format&fit=crop&q=80&w=400", "250 ml"),
            // Fruit Juices
            com.example.composecustomerapp.ui.components.Product("fj1", "Maaza Mango fruit Juice", 34, "https://images.unsplash.com/photo-1547514701-42782101795e?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("fj2", "Paper Boat Mixed berries Juice", 40, "https://images.unsplash.com/photo-1613478223719-2ab802602423?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("fj3", "B Natural Guava Juice", 48, "https://images.unsplash.com/photo-1547514701-42782101795e?auto=format&fit=crop&q=80&w=400"),
            // Energy Drinks
            com.example.composecustomerapp.ui.components.Product("ed1", "Red Bull Energy...", 125, "https://images.unsplash.com/photo-1622543925917-763c34d1538c?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("ed2", "Monster Energy...", 119, "https://images.unsplash.com/photo-1622543925917-763c34d1538c?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("ed3", "Sting Energy Drink", 30, "https://images.unsplash.com/photo-1622543925917-763c34d1538c?auto=format&fit=crop&q=80&w=400"),
            // Cookies
            com.example.composecustomerapp.ui.components.Product("c1", "Hide & Seek Chocochip Cookies", 30, "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&q=80&w=400", null, "FASTEST"),
            com.example.composecustomerapp.ui.components.Product("c2", "Sunfeast Dark Fantasy Choco fill...", 40, "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("c3", "Unibic Fruit & Nut Cookies", 70, "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=400"),
            // Cakes
            com.example.composecustomerapp.ui.components.Product("ca1", "Sunfeast Mixed fruit Cake", 30, "https://images.unsplash.com/photo-1578985545062-69928b1d9587?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("ca2", "Britannia Treat Croissant with...", 20, "https://images.unsplash.com/photo-1555507036-ab1f4038808a?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("ca3", "Lotte Choco Pie", 45, "https://images.unsplash.com/photo-1582236082449-34b8c9d1c1a5?auto=format&fit=crop&q=80&w=400"),
            // Rusks
            com.example.composecustomerapp.ui.components.Product("rw1", "Parle Real Elaichi Rusk", 54, "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?auto=format&fit=crop&q=80&w=400", "400 g"),
            com.example.composecustomerapp.ui.components.Product("rw2", "Britannia Strawberry Flavoured Wafers", 27, "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?auto=format&fit=crop&q=80&w=400", "75 g"),
            com.example.composecustomerapp.ui.components.Product("rw3", "Dukes Waffy Choco Wafer Roll", 58, "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?auto=format&fit=crop&q=80&w=400", "250 g"),
            // Noodles
            com.example.composecustomerapp.ui.components.Product("n1", "Maggie Masala...", 56, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&q=80&w=400", null, "FAST"),
            com.example.composecustomerapp.ui.components.Product("n2", "Yippee Instant...", 52, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("n3", "Korean Ramen", 44, "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?auto=format&fit=crop&q=80&w=400", null, "HOT"),
            // Soups
            com.example.composecustomerapp.ui.components.Product("s1", "Knorr Hot & Sour Vegetable Soup", 52, "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("s2", "Knorr International...", 66, "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&q=80&w=400"),
            com.example.composecustomerapp.ui.components.Product("s3", "Knorr Thick Tomato Soup", 52, "https://images.unsplash.com/photo-1547592166-23ac45744acd?auto=format&fit=crop&q=80&w=400"),
            // Frozen Foods (Cereals in screenshot)
            com.example.composecustomerapp.ui.components.Product("ff1", "Kellogg’s Corn Flakes", 152, "https://images.unsplash.com/photo-1594489053913-aa82e85b6117?auto=format&fit=crop&q=80&w=400", "1.2 kg"),
            com.example.composecustomerapp.ui.components.Product("ff2", "Saffola Classic- Masala Oats", 91, "https://images.unsplash.com/photo-1586444248902-2f64eddc13df?auto=format&fit=crop&q=80&w=400", "400 g"),
            com.example.composecustomerapp.ui.components.Product("ff3", "Kellogg’s Multigrain Chocos", 80, "https://images.unsplash.com/photo-1594489053913-aa82e85b6117?auto=format&fit=crop&q=80&w=400", "250 g")
        )

        _uiState.update { 
            it.copy(
                allProducts = products,
                activeOrders = listOf(
                    Order(
                        "65",
                        "65",
                        "Hide & Seek Chocochip Cookies",
                        30,
                        "IN_PROGRESS",
                        "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?auto=format&fit=crop&q=80&w=200",
                        "June 8, 2026"
                    ),
                    Order(
                        "66",
                        "66",
                        "Unibic Fruit & Nut Cookies",
                        70,
                        "IN_PROGRESS",
                        "https://images.unsplash.com/photo-1499636136210-6f4ee915583e?auto=format&fit=crop&q=80&w=200",
                        "June 8, 2026"
                    )
                )
            )
        }
    }

    fun setAuthenticated(isAuthenticated: Boolean) {
        _uiState.update { it.copy(isAuthenticated = isAuthenticated) }
    }

    fun updateCart(productId: String, delta: Int) {
        _uiState.update { state ->
            val currentQty = state.cartItems[productId] ?: 0
            val newQty = (currentQty + delta).coerceAtLeast(0)
            val newCartItems = state.cartItems.toMutableMap()
            if (newQty > 0) {
                newCartItems[productId] = newQty
            } else {
                newCartItems.remove(productId)
            }
            state.copy(cartItems = newCartItems)
        }
    }

    fun removeItemFromCart(productId: String) {
        _uiState.update { state ->
            val newCartItems = state.cartItems.toMutableMap()
            newCartItems.remove(productId)
            state.copy(cartItems = newCartItems)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                HomeViewModel(
                    application.categoryRepository, 
                    application.itemRepository,
                    application.orderRepository,
                    application.tokenManager
                )
            }
        }
    }
}
