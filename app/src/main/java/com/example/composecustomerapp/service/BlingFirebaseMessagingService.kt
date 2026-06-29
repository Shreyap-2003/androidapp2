package com.example.composecustomerapp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.composecustomerapp.MainActivity
import com.example.composecustomerapp.data.model.Order
import com.example.composecustomerapp.util.NotificationBus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BlingFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Always handle data payload if available
        if (remoteMessage.data.isNotEmpty()) {
            val orderId = remoteMessage.data["orderId"] ?: return
            val customerName = remoteMessage.data["customerName"] ?: "New Customer"
            val customerPhone = remoteMessage.data["customerPhone"] ?: ""
            val address = remoteMessage.data["address"] ?: "Check Dashboard"
            val itemName = remoteMessage.data["itemName"] ?: "Bling Order"
            val price = remoteMessage.data["price"]?.toDoubleOrNull() ?: 0.0
            val imageUrl = remoteMessage.data["imageUrl"] ?: ""

            val incomingOrder = Order(
                id = orderId,
                orderNumber = orderId,
                name = itemName,
                price = price,
                status = "OPEN",
                imageUrl = imageUrl,
                date = "Just now",
                customerName = customerName,
                customerPhoneNumber = customerPhone,
                customerAddress = address
            )

            // Emit to UI immediately if app is in foreground
            NotificationBus.emitOrder(incomingOrder)
            
            // Show system notification
            val title = remoteMessage.notification?.title ?: "New Order Available"
            val body = remoteMessage.notification?.body ?: "$itemName for $customerName"
            sendNotification(title, body, remoteMessage.data)
        } else {
            // Handle notification-only messages (fallback)
            remoteMessage.notification?.let {
                sendNotification(it.title ?: "New Message", it.body ?: "")
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token is typically updated on backend during Login. 
        // If the user is already logged in, we could potentially update it here too.
        println("AuthDebug: New FCM Token generated: $token")
    }

    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>? = null) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("OPEN_PARTNER_HOME", true)
            data?.forEach { (key, value) ->
                putExtra(key, value)
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "orders_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Order Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new and updated orders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}
