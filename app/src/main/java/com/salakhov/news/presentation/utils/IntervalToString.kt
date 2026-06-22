package com.salakhov.news.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.salakhov.news.R
import com.salakhov.news.domain.entities.Interval

@Composable
fun Interval.intervalToString(): String {
    return when (this) {
        Interval.MIN_15 -> stringResource(R.string._15_minutes)
        Interval.MIN_30 -> stringResource(R.string._30_minutes)
        Interval.HOUR_1 -> stringResource(R.string._1_hour)
        Interval.HOUR_6 -> stringResource(R.string._6_hours)
        Interval.HOUR_24 -> stringResource(R.string._24_hours)
    }
}