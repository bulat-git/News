package com.salakhov.news.domain.entities


data class Settings(
    val language: Language,
    val interval: Interval,
    val notificationsEnabled: Boolean,
    val wifiOnly: Boolean
) {
   companion object {
       val DEFAULT_LANGUAGE = Language.ENGLISH
       val DEFAULT_INTERVAL = Interval.MIN_15
       const val DEFAULT_NOTIFICATIONS = true
       const val DEFAULT_WIFI = false
   }
}


enum class Language {
    RUSSIAN,
    ENGLISH,
    GERMAN,
    FRENCH,
    JAPANESE
}

enum class Interval(val minutes: Int) {
    MIN_15(minutes = 15),
    MIN_30(minutes = 30),
    HOUR_1(minutes = 60),
    HOUR_6(minutes = 240),
    HOUR_24(minutes = 1440)
}