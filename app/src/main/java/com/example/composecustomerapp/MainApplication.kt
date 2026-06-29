package com.example.composecustomerapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.example.composecustomerapp.data.local.TokenManager
import com.example.composecustomerapp.data.remote.RetrofitClient
import com.example.composecustomerapp.data.repository.AuthRepository
import com.example.composecustomerapp.data.repository.CategoryRepository
import com.example.composecustomerapp.data.repository.ItemRepository
import com.example.composecustomerapp.data.repository.OrderRepository

class MainApplication : Application() {
    lateinit var tokenManager: TokenManager
    lateinit var authRepository: AuthRepository
    lateinit var categoryRepository: CategoryRepository
    lateinit var itemRepository: ItemRepository
    lateinit var orderRepository: OrderRepository

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        tokenManager = TokenManager(this)
        val authApi = RetrofitClient.createAuthApi(tokenManager)
        authRepository = AuthRepository(authApi, tokenManager)
        
        val categoryApi = RetrofitClient.createCategoryApi(tokenManager)
        categoryRepository = CategoryRepository(categoryApi)

        val itemApi = RetrofitClient.createItemApi(tokenManager)
        itemRepository = ItemRepository(itemApi)

        val orderApi = RetrofitClient.createOrderApi(tokenManager)
        orderRepository = OrderRepository(orderApi, itemApi, authApi)
    }
}
