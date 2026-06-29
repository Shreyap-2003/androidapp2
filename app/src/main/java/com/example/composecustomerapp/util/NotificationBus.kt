package com.example.composecustomerapp.util

import com.example.composecustomerapp.data.model.Order
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationBus {
    private val _newOrderEvents = MutableSharedFlow<Order>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val newOrderEvents = _newOrderEvents.asSharedFlow()

    fun emitOrder(order: Order) {
        _newOrderEvents.tryEmit(order)
    }

    fun clear() {
        _newOrderEvents.resetReplayCache()
    }
}
