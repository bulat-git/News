package com.salakhov.news.presentation.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

fun Long.toDateFormat(): String {
    return formatter.format(this)
}