package com.example.composecustomerapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.data.model.OrderResponse
import com.example.composecustomerapp.data.model.PageResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import retrofit2.Response
import kotlinx.coroutines.delay

class OrderPagingSource(
    private val fetchOrders: suspend (Int, Int) -> Response<JsonElement>,
    private val mapToDomain: suspend (List<OrderResponse>) -> List<Order>
) : PagingSource<Int, Order>() {

    private val json = Json { ignoreUnknownKeys = true }

    override fun getRefreshKey(state: PagingState<Int, Order>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Order> {
        val currentPage = params.key ?: 0
        val size = params.loadSize
        
        // Artificial delay for visibility on scroll
        if (currentPage > 0) {
            println("PagingDebug: Loading next page $currentPage...")
            delay(1500) 
        }

        return try {
            val response = fetchOrders(currentPage, size)
            if (!response.isSuccessful) {
                return LoadResult.Error(Exception("Server returned ${response.code()}"))
            }

            val body = response.body() ?: return LoadResult.Page(emptyList(), null, null)
            
            var orders: List<OrderResponse>
            var nextKey: Int?
            
            try {
                // Try parsing as metadata-wrapped PageResponse (standard for paginated APIs)
                val pageResponse = json.decodeFromJsonElement<PageResponse<OrderResponse>>(body)
                orders = pageResponse.content
                nextKey = if (currentPage < pageResponse.page.totalPages - 1) currentPage + 1 else null
                println("PagingDebug: PageResponse success. Page $currentPage of ${pageResponse.page.totalPages}")
            } catch (e: Exception) {
                // Fallback for raw List response
                orders = json.decodeFromJsonElement<List<OrderResponse>>(body)
                // If we got a full page, assume there might be more
                nextKey = if (orders.size >= size) currentPage + 1 else null
                println("PagingDebug: List response. Size: ${orders.size}, NextKey: $nextKey")
            }

            val domainOrders = mapToDomain(orders)
            
            LoadResult.Page(
                data = domainOrders,
                prevKey = if (currentPage == 0) null else currentPage - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            println("PagingDebug: Error: ${e.message}")
            LoadResult.Error(e)
        }
    }
}
