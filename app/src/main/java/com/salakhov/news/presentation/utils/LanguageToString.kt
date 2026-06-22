package com.salakhov.news.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.salakhov.news.R
import com.salakhov.news.domain.entities.Language

@Composable
fun Language.languageToString(): String {
    return when (this) {
        Language.RUSSIAN -> stringResource(R.string.russian_language)
        Language.ENGLISH -> stringResource(R.string.english_language)
        Language.GERMAN -> stringResource(R.string.german_language)
        Language.FRENCH -> stringResource(R.string.french_language)
        Language.JAPANESE -> stringResource(R.string.japanese_language)
    }
}