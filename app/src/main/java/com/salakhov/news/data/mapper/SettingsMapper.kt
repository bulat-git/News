package com.salakhov.news.data.mapper

import com.salakhov.news.domain.entities.RefreshConfig
import com.salakhov.news.domain.entities.Settings

fun Settings.toRefreshConfig(): RefreshConfig {
    return RefreshConfig(language, interval, wifiOnly)
}

