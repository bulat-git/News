package com.salakhov.news.data.local_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subscriptions")
data class SubscriptionDbModel(
    @PrimaryKey
    val topic: String
)
