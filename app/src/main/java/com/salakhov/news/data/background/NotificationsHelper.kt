package com.salakhov.news.data.background

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.salakhov.news.R
import com.salakhov.news.presentation.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationsHelper @Inject constructor(
    @param:ApplicationContext private val  context: Context
) {
    private val notificationsManager = context.getSystemService<NotificationManager>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.new_articles),
            NotificationManager.IMPORTANCE_DEFAULT
            )
        notificationsManager?.createNotificationChannel(channel)
    }

    fun showNewArticlesNotification(topics: List<String>) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            PENDING_INTENT_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_news)
            .setContentTitle(context.getString(R.string.new_articles_title))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setContentText("Update ${topics.size} subscriptions")
            .build()
        notificationsManager?.notify(NOTIFICATION_ID, notification)
    }
    companion object {
        private const val CHANNEL_ID = "new_articles"

        private const val NOTIFICATION_ID = 1

        private const val PENDING_INTENT_CODE = 1
    }
}